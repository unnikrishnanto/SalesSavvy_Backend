package com.SalesSavvy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
