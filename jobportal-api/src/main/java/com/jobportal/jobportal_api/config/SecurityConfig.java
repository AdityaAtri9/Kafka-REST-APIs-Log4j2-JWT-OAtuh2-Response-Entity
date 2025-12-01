package com.jobportal.jobportal_api.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jobportal.jobportal_api.repository.RoleRepository;
import com.jobportal.jobportal_api.repository.UserRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

	
	@Autowired
	private JwtAuthFilter jwtAuthFiler;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private JwtAuthEntryPoint jwtAuthEntryPoint;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		logger.debug("Creating BCryptPasswordEncoder bean");
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception
	{
		return cfg.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		logger.info("Configuring security filter chain");
		
		http.csrf(csrf -> csrf.disable())
		.formLogin(form -> form.disable())
		.httpBasic(basic -> basic.disable())
		.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
		.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**", "/oauth2/**", "/login/oauth2/**").permitAll().anyRequest().authenticated())
		.oauth2Login(oauth -> oauth.successHandler(new OAuth2SuccessHandler(userRepository, roleRepository, jwtUtils)));
		
		http.addFilterBefore(jwtAuthFiler, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
		
	}
}
