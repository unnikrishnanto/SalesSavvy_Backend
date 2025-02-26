package com.SalesSavvy.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.services.AdminBusinessService;

@RestController
@RequestMapping("/admin/business")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true" )
public class AdminBusinessController {
	private final AdminBusinessService adminBusinessService;

	public AdminBusinessController(AdminBusinessService adminBusinessService) {
		super();
		this.adminBusinessService = adminBusinessService;
	}
	
	
	@GetMapping("/overall")
	public ResponseEntity<?> overallBusiness() {
		try {
			Map<String, Object> monthlyBusiness = adminBusinessService.getOverallBusiness();
			return ResponseEntity.ok(monthlyBusiness);
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
		}
	}
	
	
	@GetMapping("/monthly")
	public ResponseEntity<?> getMonthlyBusiness(@RequestParam int month, @RequestParam int year) {
		try {
			Map<String, Object> monthlyBusiness = adminBusinessService.getMonthlyBusiness(month, year);
			return ResponseEntity.ok(monthlyBusiness);
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
		}
	}
	
	@GetMapping("/yearly")
	public ResponseEntity<?> yearlyBusiness(@RequestParam int year) {
		try {
			Map<String, Object> monthlyBusiness = adminBusinessService.getYearlyBusiness(year);
			return ResponseEntity.ok(monthlyBusiness);
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
		}
	}
	
	@GetMapping("/daily")
	public ResponseEntity<?> dailyBusiness(@RequestParam String date) {
		try {
			
			LocalDate localDate = LocalDate.parse(date);
			Map<String, Object> monthlyBusiness = adminBusinessService.getDayBusiness(localDate);
			return ResponseEntity.ok(monthlyBusiness);
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		}catch(DateTimeParseException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Invalid Date Format"));
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
		}
	}
	


}
