package com.InvertisAuditoriumManagement.AudiMgmt.globalexception;

public class SeatAlreadyBookedException extends Exception{
	public SeatAlreadyBookedException(String message) {
		super(message);
	}

}
