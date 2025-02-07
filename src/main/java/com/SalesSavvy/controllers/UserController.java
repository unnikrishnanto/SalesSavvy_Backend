package com.SalesSavvy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import com.SalesSavvy.entities.User;
import com.SalesSavvy.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	UserService userService;
	
	public UserController(UserService userService) {
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
	
}
