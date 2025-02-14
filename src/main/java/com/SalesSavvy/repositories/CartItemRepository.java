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
	
	@Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c where c.user.username = :username")
	int findCountOfItems(String username);
	
	@Query("SELECT c FROM CartItem c WHERE c.product.productId = :productId and  c.user.username = :username")
	Optional<CartItem> getItemByUserAndProductId(String username, int productId );
	
	@Transactional
	@Modifying
	@Query("UPDATE CartItem c SET c.quantity= c.quantity + 1 WHERE c.product.productId = :productId and  c.user.username = :username")
	int updateQuantity(String username, int productId);
}
