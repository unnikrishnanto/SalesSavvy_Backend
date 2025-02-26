package com.SalesSavvy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.OrderItem;

import jakarta.transaction.Transactional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	// Custom Query to fetch all orderItems in an order
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
	public Optional<OrderItem> findByOrderId(String orderId);
	
	// Custom Query for finding all successful orderItems for a given user
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.order.status = 'SUCCESS'")
	public List<OrderItem> findSuccessfulOrderItemsByUserId(int UserId);
	
	// Custom Query to set all the products as unavailable in orders for given productId(which is deleted)
	//(product [id = 0] is stored with values for an unavailable product)  
	@Transactional
	@Modifying
	@Query("Update OrderItem o SET o.productId = 0 WHERE o.productId = :productId")
	void setOrderItemAsUnavailable(int productId);
	
}
