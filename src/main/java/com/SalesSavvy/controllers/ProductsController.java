package com.SalesSavvy.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.dtos.ProductDTO;
import com.SalesSavvy.entities.Product;
import com.SalesSavvy.services.ProductService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="http://localhost:5173/", allowCredentials = "true", allowedHeaders = "*")
public class ProductsController {
	private ProductService productService;

	public ProductsController(ProductService productService) {
		super();
		this.productService = productService;
	}
	
	@GetMapping("/products")
	ResponseEntity<?> getProducts(@RequestParam(name = "category") String categoryName) {

		if(categoryName != null && categoryName != "") { // validating category name
			List<Product> prods = productService.getProdutsByCategory(categoryName);
			List<ProductDTO> products = new ArrayList<>();
			// Converting Product object to ProductDTO(product + image url) 
			if(prods != null) {
				for(Product prod : prods) {
					ProductDTO product = new ProductDTO(prod);
					String Url =  productService.getProductUrl(prod.getProductId());
					if(Url != null) {
						product.setImageUrl(Url);
						products.add(product);
					}
				}
				System.out.println("Sending Products");
				return ResponseEntity.ok(Map.of("Message", "Success", "products", products));
			}
		}
		
		return ResponseEntity.badRequest().body(Map.of("message", "Not a valid Category"));
		
	}
	
}
