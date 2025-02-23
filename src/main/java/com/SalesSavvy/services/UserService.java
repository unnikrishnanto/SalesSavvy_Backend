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
	
	@Transactional
	public User updateDetails(User user, String newUsername, String newEmail) throws Exception {
		userRepo.updateUserDetails(user.getUserId(), newUsername, newEmail);
		
		// Flush and clear persistence context
	    entityManager.flush();
	    entityManager.clear();
		
	    Optional<User> newUser = userRepo.findById(user.getUserId());
		if(newUser.isPresent()) {
			return newUser.get();
		}
		throw new RuntimeException("Updation Failed.");
	}	
	
	@Transactional
    public boolean changePassword(User user, String newPassword) {
    	// Encrypting new password
    	BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
    	String newEncodedPassword = passEncoder.encode(newPassword);
    	userRepo.changePassword(user.getUserId(), newEncodedPassword);
        return true;
    }
}



