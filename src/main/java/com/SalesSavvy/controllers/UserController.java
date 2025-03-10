package com.SalesSavvy.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import com.SalesSavvy.dtos.LoginDTO;
import com.SalesSavvy.dtos.UserDTO;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.UserRepository;
import com.SalesSavvy.services.AuthService;
import com.SalesSavvy.services.TokenService;
import com.SalesSavvy.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private final UserService userService;
	private final UserRepository userRepo;
	private final TokenService tokenService;
	private final AuthService authService;
	
	public UserController(UserService userService, UserRepository userRepo, TokenService tokenService,
			AuthService authService) {
		super();
		this.userService = userService;
		this.userRepo = userRepo;
		this.tokenService = tokenService;
		this.authService = authService;
	}


	@PostMapping("/register")
	@CrossOrigin
	ResponseEntity<?> registerUser(@RequestBody User user) {
		try {
			User registeredUser =  userService.userRegistration(user);
			return ResponseEntity.ok(Map.of("message", "User Resgistration Successful", "user", registeredUser));
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message" ,e.getMessage()));
		}
	}
	
	
	@GetMapping("/details")
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true" )
	ResponseEntity<?> getUserDetails(@RequestAttribute String username){		
		// converting user Object to UserDTO 
		UserDTO user = userService.getDetails(username);
		if(user != null)
			return ResponseEntity.ok(Map.of("user", user));
		
		return ResponseEntity.internalServerError().body(Map.of("message", "Something went wrong"));
	}
	
	@PostMapping("/updateDetails")
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true" )
	ResponseEntity<?> updateDetails(@RequestAttribute String username, @RequestBody Map<String, String> reqBody){
		try {
			if(username == null) {
				ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Authenticated");
			}
			Optional<User> user = userRepo.findByUsername(username);
			if(user.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Found");
			}
			
			String newUsername = reqBody.get("username");
			String newEmail = reqBody.get("email");
			
			User newUser = userService.updateDetails(user.get(), newUsername, newEmail);
			
			// For the new user name new token  must be generated using new username
			if(user.get().getUsername().equals(newUser.getUsername())) {
				return ResponseEntity.ok(Map.of("message", "success"));
			}
			
			// Delete old token
			tokenService.deleteToken(newUser.getUserId());
			
			
			// Creates and saves the token
			String token =  tokenService.createToken(newUser);
			
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
					.body(Map.of("message","Updation Successful"));

			
		} catch(DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username or Email already exists"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: " + e.getMessage());
		}
	}
	
	@PostMapping("/changePassword")
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true" )
	ResponseEntity<?> changepassword(@RequestAttribute String username, @RequestBody Map<String, String> reqBody){
		try {
			if(username == null) {
				ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Authenticated");
			}
			String oldPassword = reqBody.get("oldPassword");
			String newPassword = reqBody.get("newPassword");
			User user = authService.authenticateUser(new LoginDTO(username, oldPassword));
			if(user == null) { // on failure
				return ResponseEntity.status(401)
								.body(Map.of("message", "Wrong Old Passsword"));
			}
			
			if(userService.changePassword(user, newPassword)) {
				return ResponseEntity.ok(Map.of("message", "Password Changed Successfully."));
			}
				
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Password Change Failed"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: " + e.getMessage());
		}
		
	}
	
}
