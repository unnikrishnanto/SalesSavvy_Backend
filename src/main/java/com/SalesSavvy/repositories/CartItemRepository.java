package com.SalesSavvy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.CartItem;

import jakarta.transaction.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer>{
	
	// Custom Query to find the total count of item in cart for a particular user
	@Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c where c.user.username = :username")
	int findCountOfItems(String username);
	
	// Custom Query to fetch the cart Item by userId and produtId (For checking an items existence in cart)
	@Query("SELECT c FROM CartItem c WHERE c.product.productId = :productId and  c.user.username = :username")
	Optional<CartItem> getItemByUserAndProductId(String username, int productId );
	
	// Custom query to update cart quantity for a cart item based on productId and username
	@Transactional
	@Modifying
	@Query("UPDATE CartItem c SET c.quantity= c.quantity + 1 WHERE c.product.productId = :productId and  c.user.username = :username")
	int updateQuantity(String username, int productId);
}
