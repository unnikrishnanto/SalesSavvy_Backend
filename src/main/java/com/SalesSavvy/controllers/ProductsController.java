package com.SalesSavvy.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="http://localhost:5173/", allowCredentials = "true", allowedHeaders = "*")
public class ProductsController {
	
	@PostMapping("/products")
	String getProducts(@RequestAttribute("username") String username) {

		System.out.println(username + " is accessing payment");
		return "All Products";
	}
	
}
