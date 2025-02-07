package com.SalesSavvy.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.SalesSavvy.dtos.LoginDTO;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.UserRepository;

@Service
public class AuthService {

	private UserRepository userRepo;
	
	public AuthService(UserRepository userRepo) {
		super();
		this.userRepo = userRepo;
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
}
