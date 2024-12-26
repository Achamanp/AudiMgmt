package com.InvertisAuditoriumManagement.AudiMgmt.service;

import java.util.List;

import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EmailOrUserIdAlreadyAssociatedException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidOtpException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidSortFieldException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidTokenException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.OtpExpiredException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.UserNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.AdminSignUp;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.UpdateUserRequest;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.UserDto;

public interface UserService {
	public String registerUser(UserDto userDto) throws EmailOrUserIdAlreadyAssociatedException;

	String forgotPassword(String email) throws UserNotFoundException;

	String updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException;

	String updateEmail(Integer otp) throws InvalidOtpException, UserNotFoundException, OtpExpiredException;


	List<UserDto> getAllUser(Integer pageNumber, Integer pageSize, String sortDir, String sortBy) throws InvalidSortFieldException;

	String adminSignUp(AdminSignUp adminSignUp) throws EmailOrUserIdAlreadyAssociatedException;

	

	String sendOtpForEmailUpdate(String newEmail) throws EmailOrUserIdAlreadyAssociatedException;

	String resetPassword(String token, Integer otp, String newPassword)
			throws InvalidTokenException, OtpExpiredException, InvalidOtpException;

}
