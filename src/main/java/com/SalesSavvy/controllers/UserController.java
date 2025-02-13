package com.SalesSavvy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import com.SalesSavvy.dtos.UserDTO;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@PostMapping("/register")
	@CrossOrigin
	ResponseEntity<?> registerUser(@RequestBody User user) {
		try {
			User registeredUser =  userService.userRegistration(user);
			System.out.println("saved:" + registeredUser.getCreatedAt());
			System.out.println("saved:" + registeredUser.getUpdatedAt());
			return ResponseEntity.ok(Map.of("message", "User Resgistration Successful", "user", registeredUser));
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message" ,e.getMessage()));
		}
	}
	
	
	@GetMapping("/details")
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true" )
	ResponseEntity<?> getUserDetails(@RequestAttribute String username){
		System.out.println(username + " is Accessing");
		
		// converting user Object to UserDTO 
		UserDTO user = userService.getDetails(username);
		if(user != null)
			return ResponseEntity.ok(Map.of("user", user));
		
		return ResponseEntity.internalServerError().body(Map.of("message", "Something went wrong"));
	}
	
}
