package com.InvertisAuditoriumManagement.AudiMgmt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.Event;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.Seat;

import jakarta.persistence.LockModeType;
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long>{

	Seat findBySeatNumber(String seatNumber);

}
