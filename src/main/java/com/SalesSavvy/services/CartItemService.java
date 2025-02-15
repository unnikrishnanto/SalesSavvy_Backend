package com.SalesSavvy.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

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
		Optional<CartItem> item = cartItemRepo.getItemByUserAndProductId(username, productId);
		if(item.isPresent()) {
			cartItemRepo.updateQuantity(username, productId);
			return true;
		} 
		// if item is not present create a new CartItem 
		Optional<User> user = userRepo.findByUsername(username);
		Optional<Product> product = productRepo.findById(productId);
		if(user.isPresent()) {
			if(product.isPresent()) {
				System.out.println(cartItemRepo.save(new CartItem(user.get(), product.get(), 1)));
				return true;
			} else {
				throw new RuntimeException("Product Not found");
			}
		} else {
			throw new RuntimeException("User Not found");
		}
	}
	
}
