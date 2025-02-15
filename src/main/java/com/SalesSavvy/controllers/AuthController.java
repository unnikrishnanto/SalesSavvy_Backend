package com.SalesSavvy.controllers;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.dtos.LoginDTO;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.services.AuthService;
import com.SalesSavvy.services.TokenService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {
	private AuthService authService;
	private TokenService tokenGenService;
	
	public AuthController(AuthService authService, TokenService tokenGenService) {
		super();
		this.authService = authService;
		this.tokenGenService = tokenGenService;
	}

	@PostMapping("/login")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	ResponseEntity<?> login(@RequestBody LoginDTO loginUser) {
		
		User user = authService.authenticateUser(loginUser);
		
		if(user == null) { // on failure
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
	
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	@PostMapping("/logout")
	ResponseEntity<?> logout(@RequestAttribute String username, HttpServletResponse response){
		if(authService.logout(username)) { // the logout in service will delete the token in DB
			// setting authtoken as null in cookie to clear the existing cookie from browser 
			ResponseCookie cookie = ResponseCookie.from("authtoken", null)
					.httpOnly(true)
					.secure(false)
					.path("/")
					.sameSite("Lax")
					.domain("localhost")
					.maxAge(0)
					.build();
			
			// adding cookie to header
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
			
			return ResponseEntity.ok()
					.headers(headers)
					.body(Map.of("message", "Logout successful"));
			
		}
		return ResponseEntity.badRequest().body(Map.of("message", "Logout failed"));
	}
	
}
