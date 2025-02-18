package com.SalesSavvy.repositories;

import java.util.List;
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
	
	// Custom query to update cart quantity for a cart item based on productId and username and new quantity value 
	@Transactional
	@Modifying
	@Query("UPDATE CartItem c SET c.quantity= :quantity WHERE c.product.productId = :productId and  c.user.username = :username")
	int updateQuantity(String username, int productId, int quantity);
	
	//Custom Query to fetch all cart items for a user based on id
	@Query("SELECT c FROM CartItem c WHERE c.user.userId =:userId")
	List<CartItem> getCartItemsByUserId(int userId);
		
	
	// Custom Query to delete cart item with username and productId
	@Transactional
	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.user.username = :username AND c.product.productId = :productId")
	int deleteItemByUsernameAndProductId(String username, int productId);
	
	// Custom Query to delete all cart item for a user
	@Transactional
	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.user.userId = :userId")
	int deleteAllByUserId(int userId);
			
	
}
