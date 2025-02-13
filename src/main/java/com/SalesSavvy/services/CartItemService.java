package com.SalesSavvy.services;

import org.springframework.stereotype.Service;

import com.SalesSavvy.repositories.CartItemRepository;

@Service
public class CartItemService {
	
	private CartItemRepository cartItemRepo;
	
	public CartItemService(CartItemRepository cartItemRepo) {
		super();
		this.cartItemRepo = cartItemRepo;
	}
	
	public int getCartCount(String username) {
		return cartItemRepo.findCountOfItems(username);
	}
	
}
