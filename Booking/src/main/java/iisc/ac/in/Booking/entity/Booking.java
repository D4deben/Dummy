package iisc.ac.in.Booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	int id;
	int show_id;
	int user_id;
	int seats_booked;
	
	
	public Booking() {
		
	}
	
	public Booking( int show_id, int user_id, int seats_booked) {
		super();
		this.show_id = show_id;
		this.user_id = user_id;
		this.seats_booked = seats_booked;
	}
	
	public int getId() {
		return id;
	}

	public int getShow_id() {
		return show_id;
	}
	public void setShow_id(int show_id) {
		this.show_id = show_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getSeats_booked() {
		return seats_booked;
	}
	public void setSeats_booked(int seats_booked) {
		this.seats_booked = seats_booked;
	}
	
	
	
}
