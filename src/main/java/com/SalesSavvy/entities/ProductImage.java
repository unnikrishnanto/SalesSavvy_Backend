package com.SalesSavvy.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "productimages")
public class ProductImage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int imageId;
	
	@OneToOne
	@JoinColumn(name = "product_id", referencedColumnName = "product_id")
	Product product;
	
	@Column(name = "image_url")
	String imageURL;

	
	
	public ProductImage() {
		super();
	}

	public ProductImage(int imageId, Product product, String imageURL) {
		super();
		this.imageId = imageId;
		this.product = product;
		this.imageURL = imageURL;
	}

	public ProductImage(Product product, String imageURL) {
		super();
		this.product = product;
		this.imageURL = imageURL;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProductId(Product product) {
		this.product = product;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	
	
}
