package com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.Booking;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.Event;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.Seat;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.User;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.BookingAlreadyExistsException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.BookingNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EventNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.SeatAlreadyBookedException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.SeatNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.UserNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.BookingDto;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.BookSeatRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.EventRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.SeatRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.UserRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.service.BookSeatService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
@Service
public class BookSeatServiceImpl implements BookSeatService{
	 private final BookSeatRepository bookSeatRepository;
	    private final SeatRepository seatRepository;
	    private final UserRepository userRepository;
	    private final EventRepository eventRepository;
	    private final ModelMapper modelMapper;
	    private final HttpServletRequest request;

	    public BookSeatServiceImpl(BookSeatRepository bookSeatRepository, SeatRepository seatRepository,
	    		UserRepository userRepository,EventRepository eventRepository,ModelMapper modelMapper, HttpServletRequest request) {
	    	this.bookSeatRepository = bookSeatRepository;
	    	this.seatRepository = seatRepository;
	    	this.userRepository = userRepository;
	    	this.eventRepository = eventRepository;
	    	this.modelMapper = modelMapper;
	    	this.request = request;
	    }
	    @Override
	    @Transactional
	    public BookingDto bookTicket(String seatNumber, String eventName) 
	            throws UserNotFoundException, EventNotFoundException, BookingAlreadyExistsException, SeatNotFoundException, SeatAlreadyBookedException {
	        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
	        User user = userRepository.findByUserId(userId);
	        if (user == null) {
	            throw new UserNotFoundException("User not found with userId " + userId);
	        }

	        Booking existBooking = bookSeatRepository.findByUser(user);
	        if (existBooking != null) {
	            throw new BookingAlreadyExistsException("User already has a booking");
	        }

	        Event event = eventRepository.findByEventName(eventName);
	        if (event == null) {
	            throw new EventNotFoundException("Event not found with name " + eventName);
	        }

	        Seat seat = seatRepository.findBySeatNumber(seatNumber);
	        if (seat == null) {
	            throw new SeatNotFoundException("Seat not available");
	        }

	        boolean isSeatBooked = bookSeatRepository.existsBySeatAndEvent(seat, event);
	        if (isSeatBooked) {
	            throw new SeatAlreadyBookedException("Seat is already booked for this event");
	        }

	        try {
	            seat = seatRepository.findById(seat.getId()).orElseThrow(() -> new SeatNotFoundException("Seat not found"));
	            seat.setAvailable(false);
	            seatRepository.save(seat);

	            Booking booking = new Booking();
	            booking.setUser(user);
	            booking.setSeat(seat);
	            booking.setEvent(event);
	            booking.setBookingTime(LocalDateTime.now());

	            Booking savedBooking = bookSeatRepository.save(booking);

	            return modelMapper.map(savedBooking, BookingDto.class);

	        } catch (OptimisticLockingFailureException e) {
	            throw new OptimisticLockingFailureException("The seat or booking was updated by another transaction. Please try again.", e);
	        }
	    }


	@Override
	public List<BookingDto> getAllTicktes(Integer pageNumber, Integer pageSize, String sortDir, String sortBy) throws BookingNotFoundException {
		Sort sort = null;
		if(sortDir.equalsIgnoreCase("asc")) {
			sort = Sort.by(sortBy).ascending();
		}else {
			sort = Sort.by(sortBy).descending();
		}
		Pageable p = PageRequest.of(pageNumber, pageSize, sort);
		Page<Booking> pages = this.bookSeatRepository.findAll(p);
		if(pages == null) {
			throw new BookingNotFoundException("No bookings found for the specified criteria.");
		}
		List<Booking> bookings = pages.getContent();
		List<BookingDto> bookingDtos = bookings.stream().map(dtos-> this.modelMapper.map(dtos, BookingDto.class)).collect(Collectors.toList());
		return bookingDtos;
	}

	@Override
	public BookingDto getTicketById(String userId) throws UserNotFoundException, BookingNotFoundException {
	User user = this.userRepository.findByUserId(userId);
	if(user == null) {
		throw new  UserNotFoundException("User not found with userId "  +userId);
	}
	Booking booking = this.bookSeatRepository.findByUser(user);
	if(booking == null) {
		  throw new BookingNotFoundException("No booking found for userId " + userId);
	}
	BookingDto bookingDto = this.modelMapper.map(booking, BookingDto.class);
		return bookingDto;
		
	}

	@Override
	public String cancelTicket(String userId) throws UserNotFoundException, BookingNotFoundException {
		User user = this.userRepository.findByUserId(userId);
		if(user == null) {
			throw new  UserNotFoundException("User not found with userId "  +userId);
		}
		Booking booking = this.bookSeatRepository.findByUser(user);
		if(booking == null) {
			  throw new BookingNotFoundException("No booking found for userId " + userId);
		}
		Seat seat = booking.getSeat();
		seat.setAvailable(true);
		this.bookSeatRepository.delete(booking);
		return "Booking cancelled successfully";
	}
	
}
