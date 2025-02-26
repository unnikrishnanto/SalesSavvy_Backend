package com.SalesSavvy.dtos;

import java.time.LocalDateTime;

import com.SalesSavvy.entities.User;

public class UserDTO {
	
	private int userId;

	private String username;
	
	private String email;
	
	private String role;
	
	private LocalDateTime createdAt;
	
	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	public UserDTO(int userId, String username, String email, String role) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.role = role;
	}

	public UserDTO(int userId, String username, String email, String role, LocalDateTime createdAt) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.role = role;
		this.createdAt = createdAt;
	}

	public UserDTO(User user) {
		userId = user.getUserId();
		username = user.getUsername();
		email = user.getEmail();
		role = user.getRole().getValue();
		createdAt = user.getCreatedAt();
	}


	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	

}
