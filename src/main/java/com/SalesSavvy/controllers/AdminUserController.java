package com.SalesSavvy.controllers;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.services.AdminUserService;
import com.SalesSavvy.Exceptions.UserNotFoundException;
import com.SalesSavvy.Exceptions.InvalidRoleException;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true" )
public class AdminUserController {
	
	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		super();
		this.adminUserService = adminUserService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers(){
		try {
			return ResponseEntity.ok(Map.of("message", "Succesfuly Fetched Users", "users", adminUserService.getAllUsers()));
		}catch (Exception e) {
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
		}
	}
	
	@PostMapping("/modify")
	public ResponseEntity<?> modifyUser(@RequestBody Map<String, Object> reqBody){
		try {
			Integer userId = (Integer) reqBody.get("userId");
			String username =(String) reqBody.get("username");
			String email =(String) reqBody.get("email");
			String role = (String) reqBody.get("role");
			
			adminUserService.modifyUser(userId, username, email, role);
			
			return ResponseEntity.ok(Map.of("message", "User Modified Successfully"));
			
		}catch (IllegalArgumentException e) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		}catch(UserNotFoundException e) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}catch(InvalidRoleException e) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}catch(DataIntegrityViolationException e) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username or Email already exists"));
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: " + e.getMessage());
		}
	}
	

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteUser(@RequestBody Map<String, Integer> reqBody){
		try {
			int userId = reqBody.get("userId");
			adminUserService.deleteUser(userId);
			return ResponseEntity.ok(Map.of("message", "User Deleted Successfully"));
			
		}catch(UserNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User Not Found"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: " + e.getMessage());
		}
	}
	
	@GetMapping("/details")
	public ResponseEntity<?> getDetails(@RequestParam
			Integer userId){
		try {
			return ResponseEntity.ok(Map.of("message", "success", "user", adminUserService.getUserDetails(userId)));
		}catch(UserNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User Not Found"));
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: " + e.getMessage());
		}
	}
	
	
	
}
