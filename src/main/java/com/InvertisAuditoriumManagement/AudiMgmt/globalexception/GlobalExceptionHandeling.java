package com.InvertisAuditoriumManagement.AudiMgmt.globalexception;

import java.nio.file.InvalidPathException;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import io.jsonwebtoken.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandeling {
	@ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundEcxception(UserNotFoundException e){
    	ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND,e.getMessage());
    	return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);	
    }
	@ExceptionHandler(EmailOrUserIdAlreadyAssociatedException.class)
	public ResponseEntity<ErrorResponse> handleEmailOrUserIdAlreadyAssociatedException(EmailOrUserIdAlreadyAssociatedException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.CONFLICT);
	}
	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<ErrorResponse> invalidOtpExceptionHandler(InvalidOtpException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(IllegalArgumentException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.UNPROCESSABLE_ENTITY);
	}
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> badCredentialExceptionHandler(BadCredentialsException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.UNAUTHORIZED,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.UNAUTHORIZED);
	}
	@ExceptionHandler(EventAllReadyCreatedException.class)
	public ResponseEntity<ErrorResponse> eventAllReadyCreatedException(EventAllReadyCreatedException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.ALREADY_REPORTED, ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.ALREADY_REPORTED);
	}
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse> iOExceptionHandler(IOException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.SERVICE_UNAVAILABLE);
	}
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.PAYLOAD_TOO_LARGE);
	}
	@ExceptionHandler(InvalidPathException.class)
	public ResponseEntity<ErrorResponse> invalidPathExceptionHandler(InvalidPathException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
	}
	 @ExceptionHandler(RuntimeException.class)
	    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException ex) {
	        ErrorResponse errorResponse = new ErrorResponse(/* populate fields */);
	        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	@ExceptionHandler(EventNotFoundException.class)
	public ResponseEntity<ErrorResponse> emailNotFoundException(EventNotFoundException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<ErrorResponse> fileUploadExceptionHandler(FileUploadException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.NOT_IMPLEMENTED,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_IMPLEMENTED);
	}
	@ExceptionHandler( DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse>  dataIntegrityViolationExceptionHandler(DataIntegrityViolationException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.CONFLICT);
	}
	@ExceptionHandler(SeatNotFoundException.class)
	public ResponseEntity<ErrorResponse> seatNotFoundExceptionHandler(SeatNotFoundException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> nullPointExceptionHandler(NullPointerException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.NO_CONTENT, ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.NO_CONTENT);
	}
	@ExceptionHandler(BookingAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> bookingAlreadyExistsExceptionHandler(BookingAlreadyExistsException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.ALREADY_REPORTED,ex.getMessage());
		return new  ResponseEntity<ErrorResponse>(err, HttpStatus.ALREADY_REPORTED);
	}
	@ExceptionHandler(SeatAlreadyBookedException.class)
	public ResponseEntity<ErrorResponse> seatAllreadyBookedExceptionHandler(SeatAlreadyBookedException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.ALREADY_REPORTED,ex.getMessage());
		return new ResponseEntity<ErrorResponse>(err,HttpStatus.ALREADY_REPORTED);
	}
	@ExceptionHandler(BookingNotFoundException.class)
	public ResponseEntity<ErrorResponse> bookingNotFoundExceptionHandler(BookingNotFoundException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return new  ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(OptimisticLockingFailureException.class)
	public ResponseEntity<ErrorResponse> optimisticLockingFailureExceptionHandler(OptimisticLockingFailureException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
		return new  ResponseEntity<ErrorResponse>(err,HttpStatus.CONFLICT);
	}
	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<ErrorResponse> otpExpiredExceptionHandler(OtpExpiredException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.GONE, ex.getMessage());
		return new  ResponseEntity<ErrorResponse>(err,HttpStatus.GONE);	
	}
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorResponse> invalidTokenExceptionHandler(InvalidTokenException ex){
		ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return new  ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
	}
	

}
