package com.SalesSavvy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	
	// Fetch all successful order for a user based on userId
	
	@Query("SELECT o FROM Order o WHERE o.userId = :userId AND status= 'SUCCESS'")
	List<Order> getSuccessfulOrdersByUserId(int userId);

}
