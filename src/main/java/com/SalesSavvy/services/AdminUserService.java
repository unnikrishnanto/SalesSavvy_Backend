package com.SalesSavvy.services;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;

import com.SalesSavvy.dtos.UserDTO;
import com.SalesSavvy.entities.Role;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.CartItemRepository;
import com.SalesSavvy.repositories.OrderRepository;
import com.SalesSavvy.repositories.TokenRepository;
import com.SalesSavvy.repositories.UserRepository;
import com.SalesSavvy.Exceptions.UserNotFoundException;
import com.SalesSavvy.Exceptions.InvalidRoleException;

import jakarta.transaction.Transactional;

@Service
public class AdminUserService {
	private final UserRepository userReop;
	private final TokenRepository tokenRepo;
	private final CartItemRepository cartItemRepo;
	private final OrderRepository orderRepo;
	
	public AdminUserService(UserRepository userReop, TokenRepository tokenRepo, CartItemRepository cartItemRepo,
			OrderRepository orderRepo) {
		super();
		this.userReop = userReop;
		this.tokenRepo = tokenRepo;
		this.cartItemRepo = cartItemRepo;
		this.orderRepo = orderRepo;
	}


	public List<UserDTO> getAllUsers() {
		List<User> users = userReop.getAllUsers();
		List<UserDTO> userDtos = new ArrayList<>();
		
		for(User user : users) {
			UserDTO userDTO = new UserDTO(user);
			userDtos.add(userDTO);
		}
		return userDtos;
	}
	
	@Transactional
	public void modifyUser(int userId, String username, String email, String role) {
		User existingUser  = userReop.findById(userId)
					.orElseThrow(()-> new UserNotFoundException("User Not Found"));
		
		if(username != null && !username.isBlank()) 
			existingUser.setUsername(username);
		
		if(email != null && !email.isBlank())
			existingUser.setEmail(email);
		
		if(role != null && !role.isBlank()) {
			switch(role.toUpperCase()) {
				case "CUSTOMER":
					existingUser.setRole(Role.CUSTOMER);
					break;
				case "ADMIN":
					existingUser.setRole(Role.ADMIN);
					break;
				default:
					throw new InvalidRoleException("No such Role Exists.");
			}
		}
		
		// Deleting existing token
		tokenRepo.deleteByUserId(userId);
		
		userReop.save(existingUser);
		
	}
	
	@Transactional
	public void deleteUser(int userId) {
		User user = userReop.findById(userId)
				.orElseThrow(()-> new UserNotFoundException("User Not Found"));
	
		// Delete Token For the user
		tokenRepo.deleteByUserId(userId);
			
		// Delete all the cart Items for the user
		cartItemRepo.deleteAllByUserId(userId);
		
		// Converting all the user's transaction to userId 1 (Deactivated User)
		orderRepo.setUserAsDeactivated(userId);
		
		// Delete the user
		userReop.delete(user);
		
	}
	
	public UserDTO getUserDetails(int userId) {
		User user = userReop.findById(userId)
				.orElseThrow(()-> new UserNotFoundException("User Not Found"));
		return new UserDTO(user);
	}
	
}
