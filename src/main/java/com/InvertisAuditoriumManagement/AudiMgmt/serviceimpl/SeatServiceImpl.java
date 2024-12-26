package com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.Seat;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.SeatNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.SeatDto;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.SeatRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.service.SeatService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
@Service
public class SeatServiceImpl implements SeatService{
	@Autowired
	private EntityManager entityManager;
    private SeatRepository seatRepository;
    private ModelMapper modelMapper;
    
    public Seat findSeatWithLock(Long seatId) {
        return entityManager.find(Seat.class, seatId, LockModeType.PESSIMISTIC_WRITE);
    }
    public SeatServiceImpl(SeatRepository seatRepository, ModelMapper modelMapper) {
    	this.seatRepository = seatRepository;
    	this.modelMapper = modelMapper;
    }
    @Override
    public String createSeats(SeatDto seatDto) {
        Seat existingSeat = this.seatRepository.findBySeatNumber(seatDto.getSeatNumber());
        if (existingSeat != null) {
            throw new DataIntegrityViolationException("Seat number already exists: " + seatDto.getSeatNumber());
        }
        Seat newSeat = new Seat();
        newSeat.setAvailable(true);
        newSeat.setSeatNumber(seatDto.getSeatNumber());
        this.seatRepository.save(newSeat);

        return "Seat created successfully";
    }
    public void createBulkSeat(List<SeatDto> seatDtoList) {
        for (SeatDto seatDto : seatDtoList) {
            Seat newSeat = new Seat();
            newSeat.setAvailable(true);
            newSeat.setSeatNumber(seatDto.getSeatNumber());
            
            seatRepository.save(newSeat); 
        }
    }


	@Override
	public String updateSeat(SeatDto seatDto, String seatNumber) throws SeatNotFoundException {
		Seat seat = this.seatRepository.findBySeatNumber(seatNumber);
		if(seat == null) {
			throw new SeatNotFoundException("Seat not found with seatNumber " + seatNumber);
		}
		seat.setSeatNumber(seatDto.getSeatNumber());
		this.seatRepository.save(seat);
		return "Seat Updated Successfully";
	}

	@Override
	public String deleteSeat(String seatNumber) throws SeatNotFoundException {
		Seat seat = this.seatRepository.findBySeatNumber(seatNumber);
		if(seat == null) {
			throw new SeatNotFoundException("Seat not found with seatNumber " + seatNumber);
		}
		this.seatRepository.delete(seat);
		return "Seat deleted Successfully";
	}

	@Override
	public List<SeatDto> getAllSeats(Integer pageNumber, Integer pageSize, String sortDir, String sortBy)  {
		Sort sort = null;
		if(sortDir.equalsIgnoreCase("asc")) {
			sort = Sort.by(sortBy).ascending();
		}else {
			sort = Sort.by(sortBy).descending();
		}
		Pageable p = PageRequest.of(pageNumber, pageSize, sort);
		Page<Seat> pages = this.seatRepository.findAll(p);
		if(pages == null) {
			throw new NullPointerException("Their is no data to show ");
		}
		List<Seat> seat = pages.getContent();
		List<SeatDto> seatDto = seat.stream().map(dto-> this.modelMapper.map(dto, SeatDto.class)).collect(Collectors.toList());
		return seatDto;
	}

	@Override
	public SeatDto getBySeatNumber(String seatNumber) throws SeatNotFoundException {
		Seat seat = this.seatRepository.findBySeatNumber(seatNumber);
		 if (seat == null) {
		        throw new SeatNotFoundException("Seat with number " + seatNumber + " not found.");
		    }
		 SeatDto seatDto = this.modelMapper.map(seat, SeatDto.class);
		return seatDto;
	}

}
