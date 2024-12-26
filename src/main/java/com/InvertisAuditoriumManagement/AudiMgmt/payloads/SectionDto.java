package com.InvertisAuditoriumManagement.AudiMgmt.payloads;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


public class SectionDto {

	 @NotBlank(message = "Section name cannot be null or empty.")
	    private String sectionName;

	    @Positive(message = "Capacity must be greater than zero.")
	    private int capacity;
    private List<SeatDto> seats;
    private boolean availablity;
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public List<SeatDto> getSeats() {
		return seats;
	}
	public void setSeats(List<SeatDto> seats) {
		this.seats = seats;
	}
	public boolean isAvailablity() {
		return availablity;
	}
	public void setAvailablity(boolean availablity) {
		this.availablity = availablity;
	}

}
