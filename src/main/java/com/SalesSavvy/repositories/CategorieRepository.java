package com.SalesSavvy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.Category;

@Repository
public interface CategorieRepository extends JpaRepository<Category, Integer> {
	
	Optional<Category> findByCategoryName(String categoryName);

}
