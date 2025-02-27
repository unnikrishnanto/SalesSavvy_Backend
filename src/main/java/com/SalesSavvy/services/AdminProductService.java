package com.SalesSavvy.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.SalesSavvy.Exceptions.InvalidCategoryException;
import com.SalesSavvy.Exceptions.ProductNotFoundException;
import com.SalesSavvy.dtos.ProductDTO;
import com.SalesSavvy.entities.Category;
import com.SalesSavvy.entities.Product;
import com.SalesSavvy.entities.ProductImage;
import com.SalesSavvy.repositories.CartItemRepository;
import com.SalesSavvy.repositories.CategorieRepository;
import com.SalesSavvy.repositories.OrderItemRepository;
import com.SalesSavvy.repositories.ProductImageRepository;
import com.SalesSavvy.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminProductService {
	private final ProductRepository productRepo;
	private final ProductImageRepository productImgRepo;
	private final CategorieRepository categoryRepo;
	private final OrderItemRepository orderItemRepo;
	private final CartItemRepository cartItemRepo;
	
	public AdminProductService(ProductRepository productRepo, ProductImageRepository productImgRepo,
			CategorieRepository categoryRepo, OrderItemRepository orderItemRepo, CartItemRepository cartItemRepo) {
		super();
		this.productRepo = productRepo;
		this.productImgRepo = productImgRepo;
		this.categoryRepo = categoryRepo;
		this.orderItemRepo = orderItemRepo;
		this.cartItemRepo = cartItemRepo;
	}
	
	public List<Product> getAllProducts(){
		return productRepo.findAll();
	}
	
	public ProductDTO getProductDetails(int productId) {
		
		// Check if product is present or not
		ProductDTO productDTO = productRepo.findById(productId)
				.map(ProductDTO::new) // maps Optional<Product> to Optional<ProductDTO> 
									  // if Value is absent returns an empty Optional
				.orElseThrow(()-> new ProductNotFoundException("Invalid Product ID")); //returns value if it is present
									  // if value is absent throws exception
		
		productImgRepo.getImageByProductId(productId) 
						.map(ProductImage::getImageURL) // returns Optional<String>
									// if value is not present returns empty optional 
						.ifPresent(productDTO::setImageUrl);; // if the optional after mapping
							// is non empty sets its value to the productDTO object				
		return productDTO;
	}

	@Transactional
	public Product addProduct(String name, String description, double price, int stock, int catogory_id, String imgUrl) {
		// Validate Category using Id
		Category category = categoryRepo.findById(catogory_id)
					.orElseThrow(() -> new InvalidCategoryException("Invalid Category ID"));
		
		// Create a Product Object with the details and save it
				
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setPrice(BigDecimal.valueOf(price));
		product.setStock(stock);
		product.setCategory(category);
		product.setCreatedAt(LocalDateTime.now());
		product.setUpdatedAt(LocalDateTime.now());
		
		Product savedProduct = productRepo.save(product);
		
		if(imgUrl != null && !imgUrl.isBlank()) {
			ProductImage prodImage = new ProductImage();
			prodImage.setProductId(savedProduct);
			prodImage.setImageURL(imgUrl);
			productImgRepo.save(prodImage);
		} else {
			throw new IllegalArgumentException("Product Image URL Cannot be Empty");
		}
		
		return savedProduct;
		
	}
	
	public ProductDTO updateProduct(int productId, String name, String description, double price, int stock, int catogory_id, String imgUrl) {
		// Validate Category using Id
		Category category = categoryRepo.findById(catogory_id)
										.orElseThrow(()-> 
										 new InvalidCategoryException("Invalid Category ID"));
	
		// Check if product is present or not
		Product product = productRepo.findById(productId)
									   .orElseThrow(()->new ProductNotFoundException("Invalid Product ID"));	
		
		
		product.setName(name);
		product.setDescription(description);
		product.setPrice(BigDecimal.valueOf(price));
		product.setStock(stock);
		product.setCategory(category);
		product.setCreatedAt(LocalDateTime.now());
		product.setUpdatedAt(LocalDateTime.now());
		
		Product savedProduct = productRepo.save(product);
		
		if(imgUrl != null && !imgUrl.isBlank()) {
			Optional<ProductImage> productImgOp = productImgRepo.getImageByProductId(productId);
			
			if(productImgOp.isEmpty()) {
				ProductImage prodImage = new ProductImage();
				prodImage.setProductId(savedProduct);
				prodImage.setImageURL(imgUrl);
				productImgRepo.save(prodImage);
			} else {
				ProductImage prodImage =  productImgOp.get();
				prodImage.setImageURL(imgUrl);
				productImgRepo.save(prodImage);
			}
		} else {
			throw new IllegalArgumentException("Product Image URL Cannot be Empty");
		}
		return getProductDetails(productId);	
	}
	
	@Transactional
	public void deleteProduct(int productId) {
	
		// Check if product is present or not
		Product product = productRepo.findById(productId)
				.orElseThrow(()-> new ProductNotFoundException("Invalid Product ID"));	
		
		// Set Product as not available in case it exists in Orders
		orderItemRepo.setOrderItemAsUnavailable(productId);
		
		// Delete Product Image
		productImgRepo.deleteByProductId(productId);
		
		// Delete the product from all the carts
		cartItemRepo.deleteAllByProductId(productId);
		
		// Delete the Product
		productRepo.delete(product);
	}
	
	
}
