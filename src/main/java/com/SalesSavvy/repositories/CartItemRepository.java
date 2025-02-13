package com.SalesSavvy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer>{
	
	@Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c where c.user.username = :username")
	int findCountOfItems(String username);
	
}
