package com.SalesSavvy.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer>{
	
	// Custom query to fetch the image url associated with a product using productId
	@Query("SELECT p.imageURL FROM ProductImage p WHERE p.product.productId = :productId")
	String getUrlByProductId(int productId);
}
