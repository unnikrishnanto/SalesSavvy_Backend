package com.SalesSavvy.services;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.SalesSavvy.dtos.LoginDTO;
import com.SalesSavvy.entities.JWTToken;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.TokenRepository;
import com.SalesSavvy.repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

	private UserRepository userRepo;
	private TokenRepository tokenRepo;
	private SecretKey KEY;
	
	public AuthService(UserRepository userRepo,  TokenRepository tokenRepo , @Value("${jwt.secret}") String tokenString ) {
		super();
		this.userRepo = userRepo;
		this.tokenRepo= tokenRepo;
		KEY = Keys.hmacShaKeyFor(tokenString.getBytes());
	}




	public User  authenticateUser(LoginDTO userCred) {
		
	    Optional<User> userOp = userRepo.findByUsername(userCred.getUsername());
	    
	    if(userOp.isPresent()) {
	    	User user = userOp.get();
	    	
	    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    	if(encoder.matches(userCred.getPassword(), user.getPassword())) {
	    		return user;
	    	} else {
	    		return null;
	    	}
	    	
	    } else {
	    	return null;
	    }
		
	}
	
	public boolean validateToken( String token) {
		System.out.println("Token is: "+token);
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
					System.out.println("Expires at: " + optToken.get().getExpiresAt());
					System.out.println("Current Time: " + LocalDateTime.now());
					return optToken.get().getExpiresAt().isAfter(LocalDateTime.now());
				}
		
				return false;
			} 
//			catch (io.jsonwebtoken.security.SignatureException e) {
//				System.out.println("Invalid Token: "+ e);
//				return false;
//			} catch(ExpiredJwtException e) {
//				System.out.println("Expired Token: "+ e);
//				return false;
//			}
		catch (Exception e) {
				System.out.println(e);
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
			System.out.println(e.getMessage());
			return "";
		}
	}
}
