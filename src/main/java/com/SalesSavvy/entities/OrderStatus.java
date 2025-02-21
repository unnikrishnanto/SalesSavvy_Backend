package com.SalesSavvy.entities;

public enum OrderStatus {
	PENDING("PENDING"), 
	SUCCESS("SUCCESS"), 
	FAILED("FAILED");
	
	private String value;
	
	private OrderStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
