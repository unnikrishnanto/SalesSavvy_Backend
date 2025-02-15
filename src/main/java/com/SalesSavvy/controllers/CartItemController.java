package com.SalesSavvy.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
		// fetching cart item count with username
		int count = cartService.getCartCount(username);
		if(count >= 0) {
			return ResponseEntity.ok(Map.of("Messsage","Success", "count", count));
		}
		return ResponseEntity.internalServerError().body(Map.of("Messsage","Something went bad"));
	}
	
	@CrossOrigin(origins ="http://localhost:5173", allowCredentials = "true")
	@PostMapping("/add")
	ResponseEntity<?> addCartItem(@RequestBody Map<String, String> body){
		try {
			// extracting request body
			String username = body.get("username");
			int productId = Integer.parseInt(body.get("productId"));

			if(cartService.addCartItemByUserAndProdId(username, productId)) {
				return ResponseEntity.ok(Map.of("message", "Success"));
			}
			return ResponseEntity.ok(Map.of("message", "Falied"));
		} catch(Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
		
	}
}
