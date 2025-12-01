package com.jobportal.jobportal_api.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.jobportal_api.entity.Role;
import com.jobportal.jobportal_api.entity.User;
import com.jobportal.jobportal_api.repository.RoleRepository;
import com.jobportal.jobportal_api.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler{
	
	private static final Logger logger = LogManager.getLogger(OAuth2SuccessHandler.class);
	
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
		String email = (String) oauthUser.getAttributes().get("email");
		String name = (String) oauthUser.getAttributes().getOrDefault("name", "oauth-user");
		
		logger.info("OAuth2 login succes for email {} and name {}", email, name);
		
		Optional<User> opt = userRepo.findByEmail(email);
		User user;
		if(opt.isPresent())
		{
			user = opt.get();
			logger.debug("Existing OAuth user found {}", email);
		}
		else
		{
			user = new User();
			String username = email.split("@")[0];
			
			//Ensure username uniqueness
			int suffix = 0;
			String base = username;
			
			while(userRepo.existsByUsername(username + (suffix==0?"":"_"+suffix)))
			{
				suffix++;
			}
			
			username = username + (suffix==0?"":"_"+suffix);
			
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(""); //OAuth users don't have local password
			
			Role role = roleRepo.findByName("ROLE_USER").orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));
			user.getRole().add(role);
			
			userRepo.save(user);
			logger.info("Created local user for OAuth2: {}", username);
		}
		
		String token = jwtUtils.generateToken(user.getUsername());
		Map<String, String> body = new HashMap<>();
		body.put("token", token);
		body.put("username", user.getUsername());
		body.put("email", user.getEmail());
		
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getOutputStream(), body);
	}

}
