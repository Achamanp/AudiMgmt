package com.InvertisAuditoriumManagement.AudiMgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.Booking;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.Event;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.Seat;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.User;
@Repository
public interface BookSeatRepository extends JpaRepository<Booking, Long>{

	Booking findByUser(User user);

	boolean existsBySeatAndEvent(Seat seat, Event event);

}
