package com.SalesSavvy.dtos;

import java.math.BigDecimal;

import com.SalesSavvy.entities.Product;

public class ProductDTO {
	
	int productId;
	
	String name;
	
	String description;
	
	BigDecimal price;

	int stock;
	
	String category;
	
	String imageUrl;
	
	public ProductDTO() {
		// TODO Auto-generated constructor stub
	}

	public ProductDTO(String name, String description, BigDecimal price, int stock, String category,
			String imageUrl) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.category = category;
		this.imageUrl = imageUrl;
	}

	public ProductDTO(int productId, String name, String description, BigDecimal price, int stock, String category,
			String imageUrl) {
		super();
		this.productId = productId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.category = category;
		this.imageUrl = imageUrl;
	}
	
	public ProductDTO(Product product) {
		super();
		productId = product.getProductId();
		name = product.getName();
		description = product.getDescription();
		price = product.getPrice();
		stock = product.getStock();
		category = product.getCategory().getCategory_name();
		imageUrl = null;
	}
	
	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	
	
	
	
	
}
