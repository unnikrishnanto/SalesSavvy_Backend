package com.SalesSavvy.controllers;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.UserRepository;
import com.SalesSavvy.services.PaymentService;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	
	private PaymentService paymentService;
	private UserRepository userRepo;
	
	public PaymentController(PaymentService paymentService, UserRepository userRepo) {
		super();
		this.paymentService = paymentService;
		this.userRepo = userRepo;
	}

	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true", allowedHeaders = "*")
	@PostMapping("/create")
	ResponseEntity<?> createPaymentOrder(@RequestBody Map<String, Object> reqBody, HttpServletRequest request) {
		try {
			
			String username = request.getAttribute("username").toString();
			if(username == null) {
				ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Authenticated");
			}
			Optional<User> user = userRepo.findByUsername(username);
			if(user.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Found");
			}
			
			
			// Extract Total amount and cart items from the body
			BigDecimal totalAmount =BigDecimal.valueOf(Double.valueOf(reqBody.get("totalAmount").toString()));
	//		List<Map<String, Object>> cartItemsRaw = (List<Map<String, Object>>) reqBody.get("cartItems");
	//		
	//		List<CartItem> cartItems = cartItemsRaw.stream().map(item->{
	//			return new CartItem();
	//		}).collect(Collectors.toList());
			System.out.println("Amount is: " + totalAmount);
			String orderId = paymentService.createOrder(user.get().getUserId(), totalAmount);
			return ResponseEntity.ok(Map.of("message", "success", "orderId", orderId));
		}
		catch(RazorpayException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("could'nt create razorpay order");
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request");	
		}
	}
	
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true", allowedHeaders = "*")
	@PostMapping("/verify")
	ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> reqBody, HttpServletRequest request){
		
		try {
				String username = request.getAttribute("username").toString();
				if(username == null) {
					ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Authenticated");
				}
				Optional<User> user = userRepo.findByUsername(username);
				if(user.isEmpty()) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Found");
				}
			
				String razorpayPaymentId = reqBody.get("razorpayPaymentId").toString();
				String razorpayOrderId = reqBody.get("razorpayOrderId").toString();
				String razorpayPaymentSignature = reqBody.get("razorpaySignature").toString();
				
				boolean status = paymentService.verifyPayment(razorpayPaymentId, razorpayOrderId, razorpayPaymentSignature, user.get().getUserId());
				if(status) {
					return ResponseEntity.ok("Verification Success");
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification Failed");
				
	
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}
