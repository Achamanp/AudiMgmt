package com.InvertisAuditoriumManagement.AudiMgmt.payloads;

import java.time.LocalDateTime;


public class BookingDto {
	    private UserDto user;
	    private SeatDto seat;
	    private EventDto event;
	    private LocalDateTime bookingTime;
		public UserDto getUser() {
			return user;
		}
		public void setUser(UserDto user) {
			this.user = user;
		}
		public SeatDto getSeat() {
			return seat;
		}
		public void setSeat(SeatDto seat) {
			this.seat = seat;
		}
		public EventDto getEvent() {
			return event;
		}
		public void setEvent(EventDto event) {
			this.event = event;
		}
		public LocalDateTime getBookingTime() {
			return bookingTime;
		}
		public void setBookingTime(LocalDateTime bookingTime) {
			this.bookingTime = bookingTime;
		}

}
