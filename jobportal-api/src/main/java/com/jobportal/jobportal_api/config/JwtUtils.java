package com.jobportal.jobportal_api.config;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	
	private static final Logger logger = LogManager.getLogger(JwtUtils.class);
	@Value("${app.jwt.secret}")
	private String jwtSecret;
	
	@Value("${app.jwt.expiration-ms}")
	private long jwtExpirationMs;
	
	public String generateToken(String username)
	{
		logger.info("Generating Token for {}", username);
		return Jwts.builder()
				.subject(username)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), Jwts.SIG.HS512)
				.compact();
	}
	
	public String extractUsername(String token)
	{
		Claims claims = Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
				.build()
				.parseSignedClaims(token)
				.getPayload();
		
		return claims.getSubject();
	}
	
	public boolean isTokenValid(String token)
	{
		try {
			Claims claims = Jwts.parser()
					.verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
					.build()
					.parseSignedClaims(token) 
					.getPayload();
			
			boolean isValid = !claims.getExpiration().before(new Date());
			logger.info("Token is valid");
			return isValid;
		}
		catch(Exception e)
		{
			logger.debug("Token is invalid {}", e);
			return false;
		}
	}
}
