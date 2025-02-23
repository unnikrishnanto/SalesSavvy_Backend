package com.SalesSavvy.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.dtos.OrderDTO;
import com.SalesSavvy.dtos.UserDTO;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.UserRepository;
import com.SalesSavvy.services.OrderService;

@RestController
@RequestMapping("/api")
public class OrderController {
	
	private final OrderService orderService;
	private final UserRepository userRepo;

	public OrderController(OrderService orderService, UserRepository userRepo) {
		super();
		this.orderService = orderService;
		this.userRepo = userRepo;
	}



	@GetMapping("/orders")
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	ResponseEntity<?> getAll(@RequestAttribute String username){
		try {
	
			if(username == null) {
				ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Authenticated");
			}
			Optional<User> user = userRepo.findByUsername(username);
			if(user.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Found");
			}
			
			List<OrderDTO> orders = orderService.getAllSuccessfulOrder(user.get().getUserId());
			UserDTO userDto = new UserDTO(user.get());
			return ResponseEntity.ok(Map.of("message", "success","user", userDto, "orders", orders));	
			
		}catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: " + e.getMessage());	
		}
		
	}
	
	
	
	

}
