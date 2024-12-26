package com.InvertisAuditoriumManagement.AudiMgmt.service;

import java.util.List;

import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.BookingAlreadyExistsException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.BookingNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EventNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.SeatAlreadyBookedException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.SeatNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.UserNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.BookingDto;

public interface BookSeatService {
	public BookingDto bookTicket(String eventName,String seatNumber) throws UserNotFoundException, EventNotFoundException, BookingAlreadyExistsException, SeatAlreadyBookedException, SeatNotFoundException;
	public List<BookingDto> getAllTicktes(Integer pageNumber, Integer pageSize,String sortDir, String sortBy) throws BookingNotFoundException;
	public BookingDto getTicketById(String userid) throws UserNotFoundException, BookingNotFoundException;
	public String cancelTicket(String userId) throws UserNotFoundException, BookingNotFoundException;
	

}
