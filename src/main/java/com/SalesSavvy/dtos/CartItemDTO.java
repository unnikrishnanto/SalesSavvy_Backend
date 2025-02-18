package com.SalesSavvy.dtos;

import com.SalesSavvy.entities.CartItem;
import com.SalesSavvy.entities.Product;

public class CartItemDTO {
	private int productId;
	private String name;
	private String description;
	private String imgUrl;
	private double price;
	private int quantity;
	private double totalPrice;
	
	public CartItemDTO() {
		// TODO Auto-generated constructor stub
	}

	public CartItemDTO(int productId, String name, String description, String imgUrl, double price, int quantity,
			double totalPrice) {
		super();
		this.productId = productId;
		this.name = name;
		this.description = description;
		this.imgUrl = imgUrl;
		this.price = price;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
	}
	
	public CartItemDTO(Product product) {
		this.productId = product.getProductId();
		this.name = product.getName();
		this.description = product.getDescription();
		this.price = product.getPrice().doubleValue();
	}
	
	public CartItemDTO(CartItem cartItem) {
		this.quantity = cartItem.getQuantity();
		this.productId = cartItem.getProduct().getProductId();
		this.name = cartItem.getProduct().getName();
		this.description = cartItem.getProduct().getDescription();
		this.price = cartItem.getProduct().getPrice().doubleValue();
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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
	public void calculateAndSetTotal() {
		totalPrice = price*quantity;
	}
	
	
}
