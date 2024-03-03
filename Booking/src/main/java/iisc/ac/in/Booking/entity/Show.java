package iisc.ac.in.Booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "show")
public class Show {
	@Id
	int id;
	int theatre_id;
	String title;
	int price;
	int seats_available;
	
	public Show() {
		
	}
	
	
	
	public Show(int id, int theatre_id, String title, int price, int seats_available) {
		super();
		this.id = id;
		this.theatre_id = theatre_id;
		this.title = title;
		this.price = price;
		this.seats_available = seats_available;
	}



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTheatre_id() {
		return theatre_id;
	}
	public void setTheatre_id(int theatre_id) {
		this.theatre_id = theatre_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getSeats_available() {
		return seats_available;
	}
	public void setSeats_available(int seats_available) {
		this.seats_available = seats_available;
	}
	
	
	
}
