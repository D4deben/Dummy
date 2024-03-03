package iisc.ac.in.Booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import iisc.ac.in.Booking.entity.Booking;

public interface BookingsRepository extends JpaRepository<Booking, Integer>{

}
