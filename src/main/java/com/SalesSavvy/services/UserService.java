package com.SalesSavvy.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.SalesSavvy.dtos.UserDTO;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@PersistenceContext
	private EntityManager entityManager;

	private UserRepository userRepo;
	public UserService(UserRepository userRepo) {
		super();
		this.userRepo = userRepo;
	}

	@Transactional
	public User userRegistration(User user) {
		
		// Checks if a user with the same username already present in the DB
		if(userRepo.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("Username Already Present");
		}
		// Checks if the email id given already present in the DB
		if(userRepo.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email Id Already Exists");
		}
		
		// Encoding the password using B-crypt

		BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
		user.setPassword(passEncoder.encode(user.getPassword()));
		
		// Saving User to DB
		
		User savedUser =userRepo.save(user);
		entityManager.refresh(savedUser);
		
		System.out.println("saved:" + savedUser.getCreatedAt());
		System.out.println("saved:" + savedUser.getUpdatedAt());
		return savedUser;
	}
	
	public UserDTO getDetails(String username) {
		Optional<User> userOp = userRepo.findByUsername(username);
		if(userOp.isPresent()) {
			return new UserDTO(userOp.get());
		}
		return null;
	}
	
}
