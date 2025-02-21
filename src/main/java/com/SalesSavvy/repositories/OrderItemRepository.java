package com.SalesSavvy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
	public Optional<OrderItem> findByOrderId(String orderId);
	
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.order.status = 'SUCCESS'")
	public List<OrderItem> findSuccessfulOrderItemsByUserId(int UserId);
	
}
