package com.SalesSavvy.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.ProductImage;

import jakarta.transaction.Transactional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer>{
	
	// Custom query to fetch the image url associated with a product using productId
	@Query("SELECT p.imageURL FROM ProductImage p WHERE p.product.productId = :productId")
	String getUrlByProductId(int productId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId ")
	void deleteByProductId(int productId);
}
