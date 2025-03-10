package com.SalesSavvy.controllers;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.Exceptions.InsufficientStockException;
import com.SalesSavvy.dtos.CartItemDTO;
import com.SalesSavvy.dtos.UserDTO;
import com.SalesSavvy.entities.CartItem;
import com.SalesSavvy.services.CartItemService;
import com.SalesSavvy.services.ProductService;
import com.SalesSavvy.services.UserService;


@RestController
@RequestMapping("/api/cart")
public class CartItemController {
	private CartItemService cartService;
	private ProductService productService;
	private UserService userService;
	
	
	public CartItemController(CartItemService cartService, ProductService productService, UserService userService) {
		super();
		this.cartService = cartService;
		this.productService = productService;
		this.userService = userService;
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
		} catch(InsufficientStockException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
		
	}
	
	@CrossOrigin(origins ="http://localhost:5173", allowCredentials = "true")
	@GetMapping("/getItems")
	ResponseEntity<?> getCartItems(@RequestAttribute String username){	
		try {
			List<CartItem> cartItems = cartService.getCartItemsByUsername(username);
			List<CartItemDTO> products = new ArrayList<>();
			
			for(CartItem cartItem : cartItems) {
				CartItemDTO cartItemDTO = new CartItemDTO(cartItem);
				cartItemDTO.calculateAndSetTotal();
				if(cartItemDTO != null) {
					String imgUrl = productService.getProductUrl(cartItemDTO.getProductId());
					cartItemDTO.setImgUrl(imgUrl);
					products.add(cartItemDTO);
				}
			}
			// Fetching user details
			UserDTO user =  userService.getDetails(username);
			
			return ResponseEntity.ok(Map.of("message", "success","user", user, "products", products));
			
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
		}
	}
	
	@CrossOrigin(origins ="http://localhost:5173", allowCredentials = "true")
	@PutMapping("/update")
	ResponseEntity<?> updateCartItemQuatity(@RequestBody Map<String,String> body){
		try {
			String username = body.get("username");
			String productIdStr = body.get("productId");
			String quantityStr = body.get("quantity");
			
			int productId = Integer.parseInt(productIdStr);
			int quantity = Integer.parseInt(quantityStr);
			
			if(quantity < 0) {
				throw new InputMismatchException("Negative quantity");
			}
			
			if(quantity == 0) {
				if(cartService.removeCartItem(username, productId) > 0)
					return ResponseEntity.ok(Map.of("message", "Success"));
				else {
			        return ResponseEntity.badRequest().body(Map.of("message", "Failed to remove item"));
			    }
			}
			
			boolean updated = cartService.updateQuantity(username, productId, quantity );
			
			if(updated) {
				return ResponseEntity.ok(Map.of("message", "Success"));
			} 
			return ResponseEntity.internalServerError().body(Map.of("message", "Failed"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
		
	}
	
	
	@CrossOrigin(origins ="http://localhost:5173", allowCredentials = "true")
	@DeleteMapping("/remove")
	ResponseEntity<?> removeCartItem(@RequestBody Map<String,String> body){
		try {
			String username = body.get("username");
			String productId = body.get("productId");
			int deletedRows = cartService.removeCartItem(username, Integer.parseInt(productId));
			
			if(deletedRows > 0) 
				return ResponseEntity.ok(Map.of("message", "Success"));
			else
				return ResponseEntity.internalServerError().body(Map.of("message", "No Record Deleted"));			
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
		
	}

	@CrossOrigin(origins ="http://localhost:5173", allowCredentials = "true")
	@DeleteMapping("/removeAll")
	ResponseEntity<?> removeAllCartItems(@RequestBody Map<String,String> body){
		try {
			String userId = body.get("userId");
			
			int deletedRows = cartService.emptyCart(Integer.parseInt(userId));
			
			if(deletedRows > 0) 
				return ResponseEntity.ok(Map.of("message", "Success"));
			else
				return ResponseEntity.internalServerError().body(Map.of("message", "No Record Deleted"));			
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
		
	}
}
