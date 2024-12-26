package com.InvertisAuditoriumManagement.AudiMgmt.globalexception;

public class BookingAlreadyExistsException extends Exception {
	public BookingAlreadyExistsException(String message) {
		super(message);
	}
}
