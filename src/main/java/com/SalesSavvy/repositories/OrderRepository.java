package com.SalesSavvy.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.Order;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	
	// Fetch all successful order for a user based on userId
	
	@Query("SELECT o FROM Order o WHERE o.userId = :userId AND status= 'SUCCESS'")
	List<Order> getSuccessfulOrdersByUserId(int userId);
	
	// Change the user id to 1 (Deactivated user) for all orders of the given user
	@Transactional
	@Modifying
	@Query("Update Order o SET o.userId = 1 WHERE o.userId = :userId")
	void setUserAsDeactivated(int userId);
	
	// Get all successful orders 
	@Query("SELECT o from Order o WHERE o.status = 'SUCCESS' ")
	List<Order> getAllSuccessfulOrders();
			
	// Get successful orders of given year
	@Query("SELECT o from Order o WHERE YEAR(o.createdAt) = :year AND o.status = 'SUCCESS' ")
	List<Order> getSuccessfulOrdersByYear(int year);
	
	// Get successful orders of given month and year
	@Query("SELECT o from Order o WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year AND o.status = 'SUCCESS' ")
	List<Order> getSuccessfulOrdersBymonth(int month, int year);
	
	// Get successful orders by date
	@Query("SELECT o from Order o WHERE DATE(o.createdAt) = :date AND o.status = 'SUCCESS' ")
	List<Order> getSuccessfulOrdersByDate(LocalDate date);
		

	
}
