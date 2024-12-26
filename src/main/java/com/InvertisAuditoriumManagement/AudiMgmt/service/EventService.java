package com.InvertisAuditoriumManagement.AudiMgmt.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EventNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.EventDto;

public interface EventService {
	public String updateEvent(EventDto eventDto, String eventName) throws EventNotFoundException;
	public String deleteEvent(String eventName) throws EventNotFoundException;
	public EventDto getEventByName(String eventName) throws EventNotFoundException;
	public List<EventDto> getAllEvent(Integer pageNumber, Integer pageSize, String sortDir, String sortBy);
	String createEvent(String eventName, LocalDate startTime, String eventDescription, MultipartFile banner) throws FileUploadException;
	
}
