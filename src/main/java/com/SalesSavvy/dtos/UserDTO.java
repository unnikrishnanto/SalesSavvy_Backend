package com.SalesSavvy.dtos;

import com.SalesSavvy.entities.Role;

public class UserDTO {

	String username;
	Role role;
	
	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	public UserDTO(String username, Role role) {
		super();
		this.username = username;
		this.role= role;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	
	


}
