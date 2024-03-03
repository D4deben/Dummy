package iisc.ac.in.Booking;

import iisc.ac.in.Booking.entity.Booking;
import iisc.ac.in.Booking.entity.Show;
import iisc.ac.in.Booking.entity.Theatre;
import iisc.ac.in.Booking.repository.BookingsRepository;
import iisc.ac.in.Booking.repository.ShowRepository;
import iisc.ac.in.Booking.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;


@Service
public class BookingService {
	@Autowired
	private BookingsRepository bookingRepository;
	@Autowired
	private TheatreRepository theatreRepository;
	@Autowired
	private ShowRepository showRepository;
	
	
	
	public Theatre getTheatreById(int id) {
		return theatreRepository.findById(id).orElse(null);
	}
	
	public List<Theatre> getAllTheatres(){
		return theatreRepository.findAll();
	}
	
	public List<Show> getAllShow(){
		return showRepository.findAll();
	}
	
	public Show getShowById(int id) {
		return showRepository.findById(id).orElse(null);
	}
	
	public List<Booking> getBookingByUserId(int id){
		List<Integer> bookings = Arrays.asList(id);
		return bookingRepository.findAllById(bookings);
	}
	
	public void bookMyShow(Booking booking) {
		bookingRepository.save(booking);
	}
	
	public void deleteBookingByBookingId(int id) {
		bookingRepository.deleteById(id);
	}
	
	public void deleteAllBooking() {
		bookingRepository.deleteAll();
	}
	
	public void updateSeats(Show show) {
		showRepository.save(show);
	}

	public void saveShowData() {
			try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("shows.csv");
				 BufferedReader b = new BufferedReader(new InputStreamReader(inputStream))) {
				String s;
				Show show = new Show();
				b.readLine();
				while((s=b.readLine())!=null) {
					String []data = s.split(",");
					show.setId(Integer.parseInt(data[0]));
					show.setTheatre_id(Integer.parseInt(data[1]));
					show.setTitle(data[2]);
					show.setPrice(Integer.parseInt(data[3]));
					show.setSeats_available(Integer.parseInt(data[4]));

					showRepository.save(show);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

	public void saveTheatreData() {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("theatres.csv");
			 BufferedReader b = new BufferedReader(new InputStreamReader(inputStream))) {
			String s;
			Theatre t = new Theatre();
			b.readLine();
			while((s=b.readLine())!=null) {
				String []data = s.split(",");
				t.setId(Integer.parseInt(data[0]));
				t.setName(data[1]);
				t.setLocation(data[2]);
				
					theatreRepository.save(t);
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public List<Booking> getAllBooking(){
		return bookingRepository.findAll();
	} 
	
	
}

































