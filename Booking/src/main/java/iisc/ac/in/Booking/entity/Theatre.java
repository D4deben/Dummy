package iisc.ac.in.Booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Theatre {
	@Id
	int id;
	String name;
	String location;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

}
