package iisc.ac.in.Booking;

import iisc.ac.in.Booking.entity.Booking;
import iisc.ac.in.Booking.entity.Show;
import iisc.ac.in.Booking.entity.Theatre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BookingController {
	@Autowired
	private BookingService bookingService;

	@GetMapping("/theatres")
	public ResponseEntity<?> getAllTheatres(){
		return new ResponseEntity<>(bookingService.getAllTheatres(), HttpStatus.OK);
	}

	@GetMapping("/shows/theatres/{theatre_id}")
	public ResponseEntity<?> getShowByTheatreId(@PathVariable("theatre_id") int theatre_id){
		List<Theatre> thtr = new ArrayList<>();
		thtr = bookingService.getAllTheatres();
		int present=0;
		for(int i=0 ; i< thtr.size();i++ ) {
			if(thtr.get(i).getId()==theatre_id) {
				present = 1;
			}
		}
		if(present == 0) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		List<Show> shows = bookingService.getAllShow();

		List<Show> xshow = new ArrayList<>();
		Show shw = new Show();
		for(int i=0; i<shows.size(); i++) {
			if(shows.get(i).getTheatre_id()==theatre_id) {
				shw = shows.get(i);
				xshow.add(shw);
			}
		}

		return new ResponseEntity<>(xshow , HttpStatus.OK);
	}

	@GetMapping("/shows/{show_id}")
	public ResponseEntity<?> getShowById(@PathVariable("show_id") int show_id){
		if(bookingService.getShowById(show_id)==null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<>(bookingService.getShowById(show_id), HttpStatus.OK);
		}
	}

	@GetMapping("/bookings/users/{user_id}")
	public ResponseEntity<?> getBookingByUserId(@PathVariable("user_id") int user_id){
		try {
			URL url = new URL("http://host.docker.internal:8080/users/"+user_id);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				List<Booking> bookings = bookingService.getAllBooking();
				List<Booking> bkg = new ArrayList<>();
				for(int i=0; i<bookings.size(); i++) {
					if(bookings.get(i).getUser_id()==user_id) {
						bkg.add(bookings.get(i));
					}
				}
				return new ResponseEntity<>(bkg , HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("User Not Registered", HttpStatus.OK);
	}

	@PostMapping("/bookings")
	public ResponseEntity<?> bookMyShow(@RequestBody Booking booking) {

		if(bookingService.getShowById(booking.getShow_id())==null) {
			return new ResponseEntity<>("Show doesn't exist",HttpStatus.BAD_REQUEST);
		}
		try {
			URL url = new URL("http://host.docker.internal:8080/users/"+booking.getUser_id());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
				return new ResponseEntity<>("User doesn't exist",HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		int seats_available =  bookingService.getShowById(booking.getShow_id()).getSeats_available();
		int seats_booked = booking.getSeats_booked();

		if(seats_booked>seats_available) {
			return new ResponseEntity<>("Seats not available", HttpStatus.BAD_REQUEST);
		}
		int amount = (seats_booked)*(bookingService.getShowById(booking.getShow_id()).getPrice());

		try {
			URL url = new URL("http://host.docker.internal:8082/wallets/"+booking.getUser_id());

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			String payload = "{ \"action\":\"debit\",\"amount\": "+amount+"}";

			try(OutputStream os = connection.getOutputStream()) {
				byte[] input = payload.getBytes("utf-8");
				os.write(input, 0, input.length);
				os.flush();
			}



			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				return new ResponseEntity<>("Insufficient Balance",HttpStatus.BAD_REQUEST );
			}else {
				Show show = new Show();
				show = bookingService.getShowById(booking.getShow_id());
				int remaining = seats_available - seats_booked;
				show.setSeats_available(remaining);
				bookingService.updateSeats(show);
				bookingService.bookMyShow(booking);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		return new ResponseEntity<>("Ticket Booked Successfully", HttpStatus.OK);


	}

	@DeleteMapping("/bookings/users/{user_id}/shows/{show_id}")
	public ResponseEntity<?> deleteBookingByShow(@PathVariable("user_id") int user_id ,@PathVariable("show_id") int show_id){
		try {
			URL url = new URL("http://host.docker.internal:8080/users/"+user_id);

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if(responseCode!=HttpURLConnection.HTTP_OK) {
				return new ResponseEntity<>("User Doesn't Exist", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			List<Booking> booking_id = bookingService.getAllBooking();

			int amount;
			Show show = new Show();
			int count=0;
			for(int i =0; i< booking_id.size(); i++) {
				if(booking_id.get(i).getShow_id()==show_id && booking_id.get(i).getUser_id()==user_id) {
					count++;

					amount = booking_id.get(i).getSeats_booked()*bookingService.getShowById(booking_id.get(i).getShow_id()).getPrice();
					try {
						URL url = new URL("http://host.docker.internal:8082/wallets/"+user_id);

						HttpURLConnection connection = (HttpURLConnection)url.openConnection();
						connection.setDoOutput(true);
						connection.setRequestMethod("PUT");
						connection.setRequestProperty("Content-Type", "application/json");

						String payload = "{ \"action\":\"credit\" ,\"amount\": "+amount+"}";

						try(OutputStream os = connection.getOutputStream()) {
							byte[] input = payload.getBytes("utf-8");
							os.write(input, 0, input.length);
							os.flush();
						}
						int responseCode = connection.getResponseCode();

						if(responseCode!=HttpURLConnection.HTTP_OK) {

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					show = bookingService.getShowById(booking_id.get(i).getShow_id());
					show.setSeats_available(show.getSeats_available()+booking_id.get(i).getSeats_booked());

					bookingService.updateSeats(show);

					bookingService.deleteBookingByBookingId(booking_id.get(i).getId());
				}
			}
			if(count==0) {
				return new ResponseEntity<>("No bookings for this show_id and user_id", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>("Deleted Successfully",HttpStatus.OK);

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

	}

	@DeleteMapping("/bookings/users/{user_id}")
	public ResponseEntity<?> deleteBookingByUserId(@PathVariable("user_id") int user_id){
		try {
			URL url = new URL("http://host.docker.internal:8080/users/"+user_id);

			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if(responseCode!=HttpURLConnection.HTTP_OK) {
				return new ResponseEntity<>("User Doesn't Exist", HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}



		try {

			List<Booking> bookings = bookingService.getAllBooking();
			if(bookings.size()==0) {

				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}else {

				int amount=0;
				Show show = new Show();
				int count=0;
				for(int i=0; i<bookings.size(); i++) {
					if(bookings.get(i).getUser_id()==user_id) {

						show = bookingService.getShowById(bookings.get(i).getShow_id());
						amount += (show.getPrice()*bookings.get(i).getSeats_booked());

						show.setSeats_available(show.getSeats_available()+bookings.get(i).getSeats_booked());

						bookingService.updateSeats(show);

						bookingService.deleteBookingByBookingId(bookings.get(i).getId());
						count++;
					}

				}
				if(count==0) {

					return new ResponseEntity<String>("No bookings",HttpStatus.NOT_FOUND);
				}
				try {
					URL url = new URL("http://host.docker.internal:8082/wallets/"+user_id);

					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("PUT");
					connection.setRequestProperty("Content-Type", "application/json");
					String payload = "{ \"action\":\"credit\" ,\"amount\": "+amount+"}";

					try(OutputStream os = connection.getOutputStream()) {
						byte[] input = payload.getBytes("utf-8");
						os.write(input, 0, input.length);
						os.flush();
					}
					int responseCode = connection.getResponseCode();

					if(responseCode!=HttpURLConnection.HTTP_OK) {

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				return new ResponseEntity<>("Deleted Successfully",HttpStatus.OK );
			}

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

	}

	@DeleteMapping("/bookings")

	public ResponseEntity<?> deleteAllBooking(){
		List<Booking> bookings = bookingService.getAllBooking();
		Show show = new Show();
		int amount=0;

		for(int i=0 ; i<bookings.size(); i++) {

			show = bookingService.getShowById(bookings.get(i).getShow_id());
			amount = (bookings.get(i).getSeats_booked())*(show.getPrice());

			show.setSeats_available(show.getSeats_available()+bookings.get(i).getSeats_booked());

			try {

				URL url = new URL("http://host.docker.internal:8082/wallets/"+bookings.get(i).getUser_id());

				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);


				String payload = "{ \"action\":\"credit\",\"amount\":"+amount+"}";

				try(OutputStream os = connection.getOutputStream()) {
					byte[] input = payload.getBytes("utf-8");
					os.write(input, 0, input.length);
					os.flush();
				}

				int responsecode = connection.getResponseCode();
				if(responsecode != HttpURLConnection.HTTP_OK) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
			bookingService.updateSeats(show);

		}

		bookingService.deleteAllBooking();
		return new ResponseEntity<>("All Bookings Deleted", HttpStatus.OK);

	}



}























