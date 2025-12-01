package com.jobportal.jobportal_api.config;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jobportal.jobportal_api.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{
	
	private static final Logger logger = LogManager.getLogger(JwtAuthFilter.class);
	
	@Autowired
	private JwtUtils jwtUtils; 
	
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
		String username = null;
		String token = null;
		
		if(header != null && header.startsWith("Bearer "))
		{
			token = header.substring(7);
			if(jwtUtils.isTokenValid(token))
			{
				try {
					username = jwtUtils.extractUsername(token);
				}
				catch(Exception e)
				{
					logger.warn("could not extract username from token: {}", e.getMessage());
				}
			}
			else {
				logger.debug("JWT invalid or expired");
			}
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null)
		{
			UserDetails ud = userDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(token, null, ud.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authToken);
			logger.debug("Set SecurityContext for user {}", username);
		}
		
		filterChain.doFilter(request, response);
	}

}
