package com.SalesSavvy.entities;

public enum Role {
	ADMIN("ADMIN"),
	CUSTOMER("CUSTOMER");
	
	private String value;
	
	private Role(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
