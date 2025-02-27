package com.SalesSavvy.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SalesSavvy.Exceptions.InvalidCategoryException;
import com.SalesSavvy.Exceptions.ProductNotFoundException;
import com.SalesSavvy.dtos.ProductDTO;
import com.SalesSavvy.entities.Product;
import com.SalesSavvy.services.AdminProductService;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {
	
	private final AdminProductService adminProdService;

	public AdminProductController(AdminProductService adminProdService) {
		super();
		this.adminProdService = adminProdService;
	}
	
	@GetMapping("/all")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	ResponseEntity<?> getAll(){
		try {
			
			List<ProductDTO> responseBody = new ArrayList<>();
			for(Product product : adminProdService.getAllProducts()) {
				responseBody.add(new ProductDTO(product));
			}
			return ResponseEntity.ok(Map.of("products", responseBody));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
		}
	}
	
	
	@GetMapping("/details")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	public ResponseEntity<?> getDetails(@RequestParam int productId){
		try {
			return ResponseEntity.ok(Map.of("message", "Successfull", "product", adminProdService.getProductDetails(productId)));
			
		} catch(IllegalArgumentException | ProductNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));	
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Something went wrong"));	
		}
	}
	
	@PostMapping("/add")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> reqBody){
		
		try {
			
			String name = (String) reqBody.get("name");
			String description = (String) reqBody.get("description");
			Double price = Double.valueOf(String.valueOf(reqBody.get("price")));
			Integer catrgory_id = (Integer) reqBody.get("categoryId");
			Integer stock = (Integer) reqBody.get("stock");
			String imgUrl = (String) reqBody.get("imgUrl");
			
			Product product = adminProdService.addProduct(name, description, price, stock, catrgory_id, imgUrl);
			
			ProductDTO productDto = new ProductDTO(product); 
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Product Added Successfully", "product", productDto));
			
		} catch(IllegalArgumentException |  InvalidCategoryException e ) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));	
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Something went wrong"));	
		}
	}
	
	@PostMapping("/update")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	public ResponseEntity<?> updateProduct(@RequestBody Map<String, Object> reqBody){
		
		try {
			Integer productId = (Integer) reqBody.get("productId");
			String name = (String) reqBody.get("name");
			String description = (String) reqBody.get("description");
			Double price = Double.valueOf(String.valueOf(reqBody.get("price")));
			Integer catrgory_id = (Integer) reqBody.get("categoryId");
			Integer stock = (Integer) reqBody.get("stock");
			String imgUrl = (String) reqBody.get("imageUrl");
			ProductDTO product = adminProdService.updateProduct(productId, name, description, price, stock, catrgory_id, imgUrl);
			return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Product Added Successfully", "product", product));
			
		} catch(IllegalArgumentException |  InvalidCategoryException e ) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));	
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Something went wrong"));	
		}
	}
	
	@DeleteMapping("/delete")
	@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
	public ResponseEntity<?> deleteProduct(@RequestBody Map<String, Object> reqBody){
		try {
			Integer productId = (Integer) reqBody.get("productId");
			
			adminProdService.deleteProduct(productId);
			return ResponseEntity.ok(Map.of("message", "Product Deleted Successfully"));
			
		} catch(IllegalArgumentException | ProductNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));	
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Something went wrong"));	
		}
	}
}
