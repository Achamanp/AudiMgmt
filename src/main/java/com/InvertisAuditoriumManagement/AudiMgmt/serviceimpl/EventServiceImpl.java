package com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.Event;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EventNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.EventDto;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.EventRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.service.EventService;

@Service
public class EventServiceImpl implements EventService{
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private ModelMapper modelMapper;
	public final String UPLOAD_DIR = "static/image";
	@Override
	public String updateEvent(EventDto eventDto, String eventName) throws EventNotFoundException {
		Event event = this.eventRepository.findByEventName(eventName);
		if(event == null) {
			throw new EventNotFoundException("Event with name " + eventName + " does not exist or does not created yet");
		}
		event.setEventDescription(eventDto.getEventDescription());
		event.setEventName(eventDto.getEventName());
		event.setStartTime(eventDto.getStartTime());
		this.eventRepository.save(event);
		return "Event Updated Successfully";
	}

	@Override
	public String deleteEvent(String eventName) throws EventNotFoundException {
		Event event = this.eventRepository.findByEventName(eventName);
		if(event == null) {
			throw new EventNotFoundException("Event with name " + eventName + " does not exist or does not created yet");
		}
		this.eventRepository.delete(event);
		return "Event Deleted Successfully ";
	}

	@Override
	public EventDto getEventByName(String eventName) throws EventNotFoundException {
		Event event = this.eventRepository.findByEventName(eventName);
		if(event == null) {
			throw new EventNotFoundException("Event with name " + eventName + " does not exist or does not created yet");
		}
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		return eventDto;
	}

	@Override
	public List<EventDto> getAllEvent(Integer pageNumber, Integer pageSize, String sortDir, String sortBy) {
        Sort sort = null;
        if(sortDir.equals("asc")) {
        	sort = Sort.by(sortBy).ascending();
        }else {
        	sort = Sort.by(sortBy).descending();
        }
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        Page<Event> pages = this.eventRepository.findAll(p);
        if(pages == null) {
        	throw new NullPointerException("Their is no data to show ");
        }
        List<Event> event = pages.getContent();
        List<EventDto> eventDto = event.stream().map(dto->this.modelMapper.map(dto, EventDto.class)).collect(Collectors.toList());  
		return  eventDto;
	}
	

@Override
public String createEvent(String eventName, LocalDate startTime, String eventDescription, MultipartFile banner) throws FileUploadException {
    Event event = new Event();
    event.setEventName(eventName);
    event.setStartTime(startTime);
    event.setEventDescription(eventDescription);
    event.setCreatedOn(LocalDateTime.now());
    
    String bannerUrl = saveFile(banner);
    if (bannerUrl != null) {
        event.setBanner(bannerUrl);
    }
    this.eventRepository.save(event);

    return "Event created successfully";
}

public String saveFile(MultipartFile file) throws FileUploadException {
    try {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        // Validate file type
        if (!file.getContentType().startsWith("image/")) {
            throw new FileUploadException("Invalid file type. Only image files are allowed.");
        }

        // Ensure the upload directory exists or create it
        Path uploadDirPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        // Upload the file
        boolean isUploaded = uploadFile(file, uploadDirPath);
        if (isUploaded) {
            // Generate file download URI
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/image/")
                    .path(file.getOriginalFilename())
                    .toUriString();
        } else {
            throw new FileUploadException("Failed to upload file");
        }

    } catch (Exception e) {
        throw new FileUploadException("An error occurred while uploading the file", e);
    }
}

public boolean uploadFile(MultipartFile multipartFile, Path uploadDir) throws FileUploadException {
    try (InputStream is = multipartFile.getInputStream();
         FileOutputStream fos = new FileOutputStream(uploadDir.resolve(multipartFile.getOriginalFilename()).toFile())) {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }

        return true;
    } catch (Exception e) {
        throw new FileUploadException("Failed to save the file", e);
	}
    
}
}