package com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.EmailUpdateToken;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.PasswordResetToken;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.Role;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.User;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EmailOrUserIdAlreadyAssociatedException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidOtpException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidSortFieldException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidTokenException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.OtpExpiredException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.UserNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.AdminSignUp;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.UpdateUserRequest;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.UserDto;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.EmailUpdateTokenRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.PasswordResetTokenRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.RoleRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.UserRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.service.UserService;

import jakarta.servlet.http.HttpSession;


@Service
public class UserServiceImpl implements UserService{
	
	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private RoleRepository roleRepository;
	private JavaMailSender mailSender;
	private PasswordResetTokenRepository passwordResetTokenRepository;
	private ModelMapper modelMapper;
	private EmailUpdateTokenRepository emailUpdateTokenRepository;
	public UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder,RoleRepository roleRepository,
			JavaMailSender javaMailSender,HttpSession session, ModelMapper modelMapper,PasswordResetTokenRepository passResetTokenRepository
			,EmailUpdateTokenRepository emailUpdateTokenRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.mailSender = javaMailSender;
		this.modelMapper = modelMapper;
		this.passwordResetTokenRepository = passResetTokenRepository;
		this.emailUpdateTokenRepository = emailUpdateTokenRepository;
	}

	@Override
	public String registerUser(UserDto userDto) throws EmailOrUserIdAlreadyAssociatedException {
		 User existingUserByEmail = this.userRepository.findByEmail(userDto.getEmail());
		    User existingUserById = this.userRepository.findByUserId(userDto.getUserId());

		    if (existingUserByEmail != null || existingUserById != null) {
		        throw new EmailOrUserIdAlreadyAssociatedException("User ID or email is already associated with another account");
		    }
		    User user =  new User();
		    user.setDepartment(userDto.getDepartment());
		    user.setEmail(userDto.getEmail());
		    user.setName(userDto.getName());
		    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		    user.setUserId(userDto.getUserId());
		    Role role = this.roleRepository.findByName("STUDENT");
	        if (role == null) {
	            role = new Role();
	            role.setName("Student");
	            role = roleRepository.save(role);
	        }   
	        user.getRoles().add(role);
	        this.userRepository.save(user);
	        
	        return "Registered Successfully";
	}
	@Override
	public String forgotPassword(String email) throws UserNotFoundException {
	    try {
	        User user = userRepository.findByEmail(email);
	        if (user == null) {
	            throw new UserNotFoundException("User not found with email " + email);
	        }

	        Random rand = new Random();
	        int otp = 1000 + rand.nextInt(9000);
	        String token = UUID.randomUUID().toString();
	        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
	        PasswordResetToken resetToken = new PasswordResetToken();
	        resetToken.setOtp(otp);
	        resetToken.setToken(token);
	        resetToken.setUser(user);
	        resetToken.setExpirationTime(expirationTime);
	        passwordResetTokenRepository.save(resetToken);

	       
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("Your OTP for Password Reset");
	        message.setText("Dear " + user.getName() + ",\r\n" +
	                "\r\n" +
	                "We have received a request to reset your password. " +
	                "Use the following OTP: " + otp + " and this token: " + token + 
	                ".\r\n" +
	                "The OTP is valid for 10 minutes.\r\n");
	        mailSender.send(message);

	        return "OTP has been sent to your email.";
	        
	    } catch (UserNotFoundException e) {
	        throw e;
	    } catch (OptimisticLockingFailureException e) {
	        throw new OptimisticLockingFailureException("An error occurred while processing your request. Please try again later.", e);
	    } catch (Exception e) {
	        throw new RuntimeException("An unexpected error occurred. Please try again later.", e);
	    }
	}

	@Override
	public String resetPassword(String token, Integer otp, String newPassword)
	        throws InvalidTokenException, OtpExpiredException, InvalidOtpException {
	    PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

	    if (resetToken == null) {
	        throw new InvalidTokenException("Invalid token. Please request a new OTP.");
	    }

	    if (resetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
	        throw new OtpExpiredException("OTP has expired. Please request a new one.");
	    }

	    if (!resetToken.getOtp().equals(otp)) {
	        throw new InvalidOtpException("Invalid OTP. Please try again.");
	    }

	    try {
	        User user = resetToken.getUser();
	        user.setPassword(passwordEncoder.encode(newPassword));
	        userRepository.save(user);
	        passwordResetTokenRepository.delete(resetToken);
	    } catch (OptimisticLockingFailureException e) {
	        throw new RuntimeException("Failed to update password due to a conflict. Please try again.", e);
	    }

	    return "Password has been successfully updated.";
	}



	 @Override
	 public String updateUser(UpdateUserRequest updateUserRequest) throws UserNotFoundException {
		 String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		 User user = this.userRepository.findByUserId(userId);
		 if(user == null) {
			 throw new UserNotFoundException("User not found !");
		 }
		 user.setDepartment(updateUserRequest.getDepartment());
		 user.setName(updateUserRequest.getName());
		 user.setUserId(updateUserRequest.getUserId());
		 this.userRepository.save(user);
		 return "User updated Successfully";
		 
	 }
	 @Override
	 public String sendOtpForEmailUpdate(String newEmail) throws EmailOrUserIdAlreadyAssociatedException {
	     User existingUser = this.userRepository.findByEmail(newEmail);
	     if (existingUser != null) {
	         throw new EmailOrUserIdAlreadyAssociatedException("Email is already associated with another account.");
	     }

	     String userId = SecurityContextHolder.getContext().getAuthentication().getName();
	     User user = userRepository.findByUserId(userId);

	     // Generate OTP and expiration time
	     int otp = 1000 + new Random().nextInt(9000);
	     LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);

	     // Retrieve or create the EmailUpdateToken
	     EmailUpdateToken emailUpdateToken = emailUpdateTokenRepository.findByUser(user);
	     if (emailUpdateToken == null) {
	         emailUpdateToken = new EmailUpdateToken();
	         emailUpdateToken.setUser(user);
	     }

	     // Set the OTP and other properties
	     emailUpdateToken.setOtp(otp);
	     emailUpdateToken.setNewEmail(newEmail);
	     emailUpdateToken.setExpirationTime(expirationTime);

	     try {
	         emailUpdateTokenRepository.save(emailUpdateToken);
	     } catch (OptimisticLockingFailureException e) {
	         throw new RuntimeException("Failed to send OTP. Please try again.", e);
	     }

	     // Prepare and send the email
	     SimpleMailMessage message = new SimpleMailMessage();
	     message.setTo(newEmail);
	     message.setSubject("Email Update Request for Your Invertis Auditorium Account");
	     message.setText("Dear " + user.getName() + ",\r\n\r\n" +
	             "We have received a request to update the email address associated with your Invertis Auditorium account. " +
	             "To proceed with this update, please use the following One-Time Password (OTP):\r\n\r\n" +
	             "OTP: " + otp + "\r\n\r\n" +
	             "This OTP is valid for the next 10 minutes. Please enter this code on the email update page to confirm the change. " +
	             "If you did not request this email update, please ignore this email, and your email address will remain unchanged.\r\n\r\n" +
	             "Best regards,\r\nInvertis University");

	     mailSender.send(message);

	     return "OTP has been sent to your new email address.";
	 }

	 @Transactional
	 public String updateEmail(Integer otp) throws OtpExpiredException, InvalidOtpException, UserNotFoundException {
	     String userId = SecurityContextHolder.getContext().getAuthentication().getName();
	     User user = this.userRepository.findByUserId(userId);
	     
	     if (user == null) {
	         throw new UserNotFoundException("User not found.");
	     }

	     EmailUpdateToken emailUpdateToken = this.emailUpdateTokenRepository.findByUser(user);
	     
	     if (emailUpdateToken == null) {
	         throw new InvalidOtpException("No OTP request found. Please try again.");
	     }
	     
	     if (!emailUpdateToken.getOtp().equals(otp)) {
	         throw new InvalidOtpException("Invalid OTP. Please try again.");
	     }
	     
	     if (LocalDateTime.now().isAfter(emailUpdateToken.getExpirationTime())) {
	         throw new OtpExpiredException("The OTP has expired. Please request a new one.");
	     }

	     try {
	         // Update the user's email
	         user.setEmail(emailUpdateToken.getNewEmail());
	         userRepository.save(user); // This should save the user with the new email

	         // Explicitly delete the EmailUpdateToken
	         emailUpdateTokenRepository.delete(emailUpdateToken);
	         logger.info("Deleted OTP for user: {}", user.getUserId());
	     } catch (OptimisticLockingFailureException e) {
	         throw new RuntimeException("Failed to update email. Please try again.", e);
	     }

	     return "Your email has been updated successfully.";
	 }
		@Override
		public List<UserDto> getAllUser(Integer pageNumber, Integer pageSize, String sortDir, String sortBy) throws InvalidSortFieldException {
		    if (pageNumber < 0) {
		        throw new IllegalArgumentException("Page number must be non-negative.");
		    }
		    if (pageSize == null || pageSize <= 0) {
		        pageSize = 10; 
		    }
		    List<String> validSortFields = Arrays.asList("userId", "name", "email"); 
		    if (sortBy != null && !validSortFields.contains(sortBy)) {
		        throw new InvalidSortFieldException("Invalid sort field: " + sortBy);
		    }
		    Sort sort;
		    if (sortDir == null || !sortDir.equalsIgnoreCase("asc")) {
		        sort = Sort.by(sortBy != null ? sortBy : validSortFields.get(0)).descending(); 
		    } else {
		        sort = Sort.by(sortBy != null ? sortBy : validSortFields.get(0)).ascending(); 
		    }
		    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		    Page<User> pages = this.userRepository.findAll(pageable);
		    if(pages == null) {
	        	throw new NullPointerException("Their is no data to show ");
	        }
		    List<User> users = pages.getContent();
		    List<UserDto> userDtos = users.stream()
                    .map(user -> {
                        UserDto userDto = this.modelMapper.map(user, UserDto.class);
                        userDto.setPassword(null); 
                        return userDto;
                    })
                    .collect(Collectors.toList());
		    
		    return userDtos;
		}
		@Override
		public String adminSignUp(AdminSignUp adminSignUp) throws EmailOrUserIdAlreadyAssociatedException {
			 User existingUserByEmail = this.userRepository.findByEmail(adminSignUp.getEmail());
			    User existingUserById = this.userRepository.findByUserId(adminSignUp.getUserId());

			    if (existingUserByEmail != null || existingUserById != null) {
			        throw new EmailOrUserIdAlreadyAssociatedException("User ID or email is already associated with another account");
			    }
			    User user =  new User();
			    user.setEmail(adminSignUp.getEmail());
			    user.setName(adminSignUp.getName());
			    user.setPassword(passwordEncoder.encode(adminSignUp.getPassword()));
			    user.setUserId(adminSignUp.getUserId());
			    user.setDepartment(adminSignUp.getDepartment());
			    Role role = this.roleRepository.findByName("ADMIN");
		        if (role == null) {
		            role = new Role();
		            role.setName("ADMIN");
		            role = roleRepository.save(role);
		        }   
		        user.getRoles().add(role);
		        this.userRepository.save(user);
		        
		        return "Registered Successfully";
			
		}
		 public void createUsers(List<UserDto> userDtos) {
		        for (UserDto userDto : userDtos) {
		            String encodedPassword = passwordEncoder.encode(userDto.getPassword());

		            User user = new User();
		            user.setUserId(userDto.getUserId());
		            user.setName(userDto.getName());
		            user.setEmail(userDto.getEmail());
		            user.setPassword(encodedPassword);  
		            user.setDepartment(userDto.getDepartment());
		            Role role = this.roleRepository.findByName("STUDENT");
		            if (role == null) {
		                role = new Role();
		                role.setName("STUDENT");
		                role = roleRepository.save(role);
		            }
		            user.getRoles().add(role);
		            this.userRepository.save(user);
		        }
		 }
}
