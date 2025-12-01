package com.jobportal.jobportal_api.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jobportal.jobportal_api.entity.Role;
import com.jobportal.jobportal_api.entity.User;
import com.jobportal.jobportal_api.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);
	
	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		
		String[] authorities = user.getRole().stream().map(Role::getName).toArray(String []::new);
		
		logger.debug("Loaded user {} with autorities {}", username, authorities);
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername())
				.password(user.getPassword() == null ? "" : user.getPassword())
				.authorities(authorities)
				.build();
	}
	
	
}
