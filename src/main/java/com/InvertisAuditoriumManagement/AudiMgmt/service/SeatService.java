package com.InvertisAuditoriumManagement.AudiMgmt.service;

import java.util.List;

import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.SeatNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.SeatDto;

public interface SeatService {
	public String createSeats(SeatDto seatDto);
	public String updateSeat(SeatDto seatDto,String seatNumber) throws SeatNotFoundException;
	public String deleteSeat(String seatNumber) throws SeatNotFoundException;
	public List<SeatDto> getAllSeats(Integer pageNumber, Integer pageSize, String sortDir, String sortBy) throws SeatNotFoundException;
	public SeatDto getBySeatNumber(String seatNumber) throws SeatNotFoundException;

}
