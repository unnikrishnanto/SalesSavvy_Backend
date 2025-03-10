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
public class TokenService {
	
	@Value("${jwt.secret}")
	private String tokenGenrationKey;
	
	@Value("${jwt.expiration}")
	private String tokenExpiration;
	
	private SecretKey KEY;

	
	private TokenRepository tokenRepo;
	
	public TokenService(TokenRepository tokenRepo, @Value("${string_for_key}") String stringForTokenKey ) {
		super();
		this.tokenRepo = tokenRepo;
		this.KEY = Keys.hmacShaKeyFor(stringForTokenKey.getBytes());
		
	}

	public String createToken(User user) {
		
		Optional<JWTToken> oldToken = tokenRepo.findByUserId(user.getUserId());
		
		if(oldToken.isPresent() && LocalDateTime.now().isBefore(oldToken.get().getExpiresAt())){
			return oldToken.get().getToken();
		}else {
			
			if(oldToken.isPresent()) {
				tokenRepo.deleteByUserId(oldToken.get().getUser().getUserId());
			}
	
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
	
	public boolean validateToken( String token) {
		if(token !=null) {
			try {
				
				// validating the token by parsing using key
				Jwts.parserBuilder()
						.setSigningKey(KEY)
						.build()
						.parseClaimsJws(token);

				
				// checking if the token exists in the DB and Not expired
				Optional<JWTToken> optToken = tokenRepo.findByToken(token);
				if(optToken.isPresent()) {
					return optToken.get().getExpiresAt().isAfter(LocalDateTime.now());
				}
		
				return false;
			} 
//			catch (io.jsonwebtoken.security.SignatureException e) {
//				return false;
//			} catch(ExpiredJwtException e) {
//				return false;
//			}
		catch (Exception e) {
			    e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	
	public String extractUsername(String token) {
		try {
		 return	Jwts.parserBuilder()
			.setSigningKey(KEY)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
		
		} catch(Exception e) {
			return "";
		}
	}
	
	
	public void deleteToken(int userId) {
		tokenRepo.deleteByUserId(userId);
	}
	
	
}
