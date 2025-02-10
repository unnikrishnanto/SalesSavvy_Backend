package com.SalesSavvy.services;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.SalesSavvy.entities.JWTToken;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.TokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenGenerationService {
	
	@Value("${jwt.secret}")
	private String tokenGenrationKey;
	
	@Value("${jwt.expiration}")
	private String tokenExpiration;
	private TokenRepository tokenRepo;
	
	
	public TokenGenerationService(TokenRepository tokenRepo) {
		super();
		this.tokenRepo = tokenRepo;
	}

	public String createToken(User user) {
		
		Optional<JWTToken> oldToken = tokenRepo.findByUserId(user.getUserId());
		
		if(oldToken.isPresent() && LocalDateTime.now().isBefore(oldToken.get().getExpiresAt())){
			System.out.println("Sending back old token");
			return oldToken.get().getToken();
		}else {
			
			if(oldToken.isPresent()) {
				tokenRepo.deleteByUserId(oldToken.get().getUser().getUserId());
			}
			System.out.println("deleting old token");
			
			System.out.println("creating new token");
			//create New Token
			String newToken = createNewTOken(user); 
			
			return newToken;
		}
		
	}

	private String createNewTOken(User user) {
		// Generating a key using the string from properties  
		SecretKey key = Keys.hmacShaKeyFor(tokenGenrationKey.getBytes());
	
		
		LocalDateTime creatdAt = LocalDateTime.now();
		LocalDateTime expiresAt = creatdAt.plusSeconds(Long.parseLong(tokenExpiration));
		
		
		// Creating a JSON Web Token
		String tokenString = Jwts.builder()
		.setSubject(user.getUsername())
		.claim("role",user.getRole().getValue())
		.signWith(key)
		.setIssuedAt(new Date())
		.setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(tokenExpiration)*1000))
		.compact();
		
		// Saving the Token in the DB
		tokenRepo.save(	new JWTToken(user, tokenString ,expiresAt));
		return tokenString;
					
		
	}
	
	
	
}
