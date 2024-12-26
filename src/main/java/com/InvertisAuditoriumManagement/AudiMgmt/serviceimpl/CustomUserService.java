package com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.User;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.UserRepository;
@Service
public class CustomUserService implements UserDetailsService{
	private UserRepository userRepository;
	public CustomUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		User user = this.userRepository.findByUserId(userId);
		if(user == null) {
			throw new UsernameNotFoundException("User not found with userId " + userId);
		}
		return user;
	}

}
