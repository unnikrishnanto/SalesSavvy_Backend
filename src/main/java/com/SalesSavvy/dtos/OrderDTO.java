package com.SalesSavvy.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.SalesSavvy.entities.OrderItem;
import com.SalesSavvy.entities.Product;

public class OrderDTO {
	
	private String OrderId;
	private int productId;
	private int quantity;
	private BigDecimal pricePerUnit;
	private BigDecimal totalAmount;
	private String name;
	private String description;
	private String imgUrl;
	private LocalDateTime time;
	
	
	public OrderDTO() {
		// TODO Auto-generated constructor stub
	}


	public OrderDTO(String orderId, int productId, int quantity, BigDecimal pricePerUnit, BigDecimal totalAmount,
			String name, String description, String imgUrl, LocalDateTime time) {
		super();
		OrderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
		this.pricePerUnit = pricePerUnit;
		this.totalAmount = totalAmount;
		this.name = name;
		this.description = description;
		this.imgUrl = imgUrl;
		this.time = time;
	}
	
	public void setWithOrderItem(OrderItem orderItem) {
		productId = orderItem.getProductId();
		quantity = orderItem.getQuantity();
		pricePerUnit = orderItem.getPricePerUnit();
		totalAmount = orderItem.getTotalPrice();
	}
	
	public void setWithProduct(Product product) {
		productId = product.getProductId();
		name = product.getName();
		description = product.getDescription();
	}


	public String getOrderId() {
		return OrderId;
	}


	public void setOrderId(String orderId) {
		OrderId = orderId;
	}


	public int getProductId() {
		return productId;
	}


	public void setProductId(int productId) {
		this.productId = productId;
	}


	public int getQuantity() {
		return quantity;
	}


	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}


	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}


	public BigDecimal getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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


	public LocalDateTime getTime() {
		return time;
	}


	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	
	
	

}
