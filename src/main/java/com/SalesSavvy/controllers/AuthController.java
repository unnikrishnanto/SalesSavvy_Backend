package com.SalesSavvy.controllers;
import java.time.Duration;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.dtos.LoginDTO;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.services.AuthService;
import com.SalesSavvy.services.TokenGenarationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api")
public class AuthController {
	private AuthService authService;
	private TokenGenarationService tokenGenService;
	
	@Value("${jwt.secret}")
	private String tokenString;
	
	public AuthController(AuthService authService, TokenGenarationService tokenGenService) {
		super();
		this.authService = authService;
		this.tokenGenService = tokenGenService;
	}



	@PostMapping("/login")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	ResponseEntity<?> login(@RequestBody LoginDTO loginUser) {
		
		User user = authService.authenticateUser(loginUser);
		
		
		if(user == null) {
			return ResponseEntity.status(401)
							.body(Map.of("message", "Invalid Username Or Passsword"));
		}
		System.out.println("Genreating token for cookie...");
		
		// Creates and saves the token
		String token =  tokenGenService.createToken(user);
		
		// send the token as Cookie
		
		ResponseCookie cookie = ResponseCookie.from("authtoken", token)
				.httpOnly(true)  // Prevents JavaScript access
	            .secure(false)   // Set `true` only if using HTTPS
	            .path("/")       // Available for all routes
	            .sameSite("Lax") // Allows cross-origin with user interaction
	            .domain("localhost") // Set explicitly for localhost
	            .maxAge(Duration.ofDays(1)) // Expires in 1 day
	            .build();
		
		// Adding the cookie to the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

		
		return ResponseEntity.ok()
				.headers(headers)
				.body(Map.of("role", user.getRole().getValue(), "message","Login Successful"));
		
	}
	
	@PostMapping("/validate")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	String validate (@CookieValue(name="authtoken" , required = false) String token) {
		System.out.println("HELLO");
		System.out.println(token);
		try {
			SecretKey key = Keys.hmacShaKeyFor(tokenString.getBytes());

			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();
			String subject = claims.getSubject();
			String role = claims.get("role", String.class);
			java.util.Date expDate = claims.getExpiration();
			
			System.out.println(subject);
			System.out.println(role);
			System.out.println(expDate);
			return "valid Token";
		} catch (io.jsonwebtoken.security.SignatureException e) {
			System.out.println("Invalid Token: "+ e);
			return "Invalid Token";
		} catch(ExpiredJwtException e) {
			System.out.println("Expired Token: "+ e);
			return "TOken Expired";
		}catch (Exception e) {
			System.out.println(e);
			return e.getMessage();
		}

	}
	
}
