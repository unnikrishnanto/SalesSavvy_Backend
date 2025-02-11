package com.SalesSavvy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{
	
	@Query("SELECT p FROM Product p WHERE p.category.categoryId = :categoryId ")
	List<Product> findByCategoryId(int categoryId);
}
