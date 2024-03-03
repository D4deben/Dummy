package iisc.ac.in.Booking;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class BookingsApplication implements CommandLineRunner {

	@Autowired
	private BookingService bookingService;
	public static void main(String[] args) {
				SpringApplication.run(BookingsApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception{
	       bookingService.saveShowData();
	       bookingService.saveTheatreData();
	}

}
