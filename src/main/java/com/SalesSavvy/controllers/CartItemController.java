package com.SalesSavvy.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.services.CartItemService;

@RestController
@RequestMapping("/api/cart")
public class CartItemController {
	CartItemService cartService;

	public CartItemController(CartItemService cartService) {
		super();
		this.cartService = cartService;
	}

	@GetMapping("/count")
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	ResponseEntity<?> count(@RequestAttribute String username){
		int count = cartService.getCartCount(username);
		if(count >= 0) {
			return ResponseEntity.ok(Map.of("Messsage","Success", "count", count));
		}
		return ResponseEntity.internalServerError().body(Map.of("Messsage","Something went bad"));
	}
}
