package com.SalesSavvy.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.SalesSavvy.entities.Category;
import com.SalesSavvy.entities.Product;
import com.SalesSavvy.repositories.CategorieRepository;
import com.SalesSavvy.repositories.ProductImageRepository;
import com.SalesSavvy.repositories.ProductRepository;

@Service
public class ProductService {
	
	private ProductRepository prodRepo;
	private ProductImageRepository prodImgRepo;
	private CategorieRepository categoryRepo;
	
	public ProductService(ProductRepository prodRepo, ProductImageRepository prodImgRepo,
			CategorieRepository categoryRepo) {
		super();
		this.prodRepo = prodRepo;
		this.prodImgRepo = prodImgRepo;
		this.categoryRepo = categoryRepo;
	}
	
	
	public List<Product> getProdutsByCategory(String categoryName){
		
		Optional<Category> category = categoryRepo.findByCategoryName(categoryName);
		
		if(category.isPresent()) {
			List<Product> products = prodRepo.findByCategoryId(category.get().getCategoryId())
;
			return products;
			}
		
		return null;
	}
	
	public String getProductUrl(int productId) {
		return prodImgRepo.getUrlByProductId(productId);
	}
	
}
