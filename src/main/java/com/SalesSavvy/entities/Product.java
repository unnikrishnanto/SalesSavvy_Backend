package com.SalesSavvy.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "products")
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="product_id")
	int productId;
	
	@Column
	String name;
	
	@Column
	String description;
	
	@Column
	BigDecimal price;
	
	@Column
	int stock;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	Category category;
	
	
	@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
	LocalDateTime createdAt;
	
	@Column(nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
	LocalDateTime updatedAt;
	
	
	public Product() {
		// TODO Auto-generated constructor stub
	}

	public Product(String name, String description, BigDecimal price, int stock, Category category,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.category = category;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Product(int productId, String name, String description, BigDecimal price, int stock, Category category,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.productId = productId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.category = category;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getProductId() {
		return productId;
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

		
	
	
}
