package com.SalesSavvy.services;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.SalesSavvy.Exceptions.InsufficientStockException;
import com.SalesSavvy.Exceptions.ProductNotFoundException;
import com.SalesSavvy.Exceptions.UserNotFoundException;
import com.SalesSavvy.entities.CartItem;
import com.SalesSavvy.entities.Product;
import com.SalesSavvy.entities.User;
import com.SalesSavvy.repositories.CartItemRepository;
import com.SalesSavvy.repositories.ProductRepository;
import com.SalesSavvy.repositories.UserRepository;

@Service
public class CartItemService {
	
	private CartItemRepository cartItemRepo;
	private ProductRepository productRepo;
	private UserRepository userRepo;
	
	
	
	public CartItemService(CartItemRepository cartItemRepo, ProductRepository productRepo, UserRepository userRepo) {
		super();
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.userRepo = userRepo;
	}

	public int getCartCount(String username) {
		return cartItemRepo.findCountOfItems(username);
	}
	
	public boolean addCartItemByUserAndProdId(String username, int productId) {
		
		Product product = productRepo.findById(productId)
							.orElseThrow(()-> new ProductNotFoundException("Product Not Found"));
		
		User user = userRepo.findByUsername(username)
				.orElseThrow(()-> new UserNotFoundException("User Not Found"));
		
		if(product.getStock() <=0 ) {
			throw new InsufficientStockException("Insufficient stock available to complete the update");
		}
		
		CartItem item = cartItemRepo.getItemByUserAndProductId(username, productId).orElse(null);
		
		if(item != null) {
			int updatedQuantity =  item.getQuantity()+1;
			if(updatedQuantity > product.getStock()) {
				throw new InsufficientStockException("Insufficient stock available to complete the update. Please adjust the quantity.");
			}
			cartItemRepo.updateQuantity(username, productId, updatedQuantity);
		} else {
		// if item is not present create a new CartItem 
	
		cartItemRepo.save(new CartItem(user, product, 1));
		}
		return true;
		
	}
	
	
	public List<CartItem> getCartItemsByUsername(String username){
		
		return userRepo.findByUsername(username)
			.map(user -> cartItemRepo.getCartItemsByUserId(user.getUserId()))
			.orElse(Collections.emptyList());
	}
	
	public boolean updateQuantity(String username, int productId, int quantity) {
		Product product = productRepo.findById(productId)
				.orElseThrow(()-> new ProductNotFoundException("Product Not Found"));

		if(product.getStock() <=0 ) {
			throw new InsufficientStockException("Insufficient stock available to complete the update");
		}
		
		if(quantity > product.getStock()) {
			throw new InsufficientStockException("Insufficient stock available to complete the update");
		}
		
		if(cartItemRepo.updateQuantity(username, productId, quantity) > 0)
			return true;
		else 
			return false;
	}
	
	public int removeCartItem(String username, int productId) {
		return cartItemRepo.deleteItemByUsernameAndProductId(username, productId);
	}
	
	public int emptyCart(int userId) {
		return cartItemRepo.deleteAllByUserId(userId);
	}
		
}
