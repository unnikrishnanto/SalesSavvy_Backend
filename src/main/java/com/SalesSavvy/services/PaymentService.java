package com.SalesSavvy.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.SalesSavvy.entities.CartItem;
import com.SalesSavvy.entities.Order;
import com.SalesSavvy.entities.OrderItem;
import com.SalesSavvy.entities.OrderStatus;
import com.SalesSavvy.entities.Product;
import com.SalesSavvy.repositories.CartItemRepository;
import com.SalesSavvy.repositories.OrderItemRepository;
import com.SalesSavvy.repositories.OrderRepository;
import com.SalesSavvy.repositories.ProductRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {
	
	@Value("${razorpay.key_id}")
	private String RAZORPAY_KEY_ID;
	
	@Value("${razorpay.key_secret}")
	private  String RAZORPAY_KEY_SECRET ;


	private final OrderRepository orderRepo;
	private final OrderItemRepository orderItemRepo;
	private final CartItemRepository cartItemRepo;
	private final ProductRepository productRepo;
	
	public PaymentService(OrderRepository orderRepo, OrderItemRepository orderItemRepo, CartItemRepository cartItemRepo,
			ProductRepository productRepo) {
		super();
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
	}

	@Transactional
	public String createOrder(int userId, BigDecimal totalAmount) throws RazorpayException {
		
		// creating a razorpay client using credentials
		RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);
		
		// Prepare Razorpay order request
		var orderRequest = new JSONObject();
		orderRequest.put("amount", (totalAmount.multiply(BigDecimal.valueOf(100)).intValue()));
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
		
		// Create Razor Pay Order
		
		com.razorpay.Order rzPayOrder =  razorpayClient.orders.create(orderRequest);
		
		 // Save order details in the database
		Order order = new Order();
		
		order.setOrderId(rzPayOrder.get("id"));
		order.setUserId(userId);
		order.setStatus(OrderStatus.PENDING);
		order.setTotalAmount(totalAmount);
		order.setCreatedAt(LocalDateTime.now());
		
		orderRepo.save(order);
		
		return rzPayOrder.get("id");		
	}
	
	@Transactional
	public boolean verifyPayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature, int userId) {
		
	 try {	
			// prepare signature validation attributes
	
	        JSONObject attributes = new JSONObject();
	        attributes.put("razorpay_order_id", razorpayOrderId);
	        attributes.put("razorpay_payment_id", razorpayPaymentId);
	        attributes.put("razorpay_signature", razorpaySignature);
	
	        // Verify Razorpay signature
	        boolean isSignatureValid = com.razorpay.Utils.verifyPaymentSignature(attributes, RAZORPAY_KEY_SECRET);
	
			if (isSignatureValid) {
				Order order = orderRepo.findById(razorpayOrderId)
						.orElseThrow(()-> new RuntimeException("Order not found"));
				
				order.setStatus(OrderStatus.SUCCESS);
				order.setUpdatedAt(LocalDateTime.now());
				
				orderRepo.save(order);
				
				
				// Saving CartItem 's as OrderItem 's in the OrderItem table  and update stock
				
				
				// Fetch CartItems of the user
				List<CartItem> cartItems = cartItemRepo.getCartItemsByUserId(userId);
				 if (cartItems.isEmpty()) {
			            throw new RuntimeException("No items in cart to process the order.");
			        }

		        // Fetch all products in a single query
		        List<Integer> productIds = cartItems.stream()
		                .map(cartItem -> cartItem.getProduct().getProductId())
		                .collect(Collectors.toList());

		        List<Product> products = productRepo.findAllById(productIds);
		        Map<Integer, Product> productMap = products.stream()
		                .collect(Collectors.toMap(Product::getProductId, p -> p));

		        List<OrderItem> orderItems = new ArrayList<>();
				
		        // Prepare OrderItems and update stock
				for(CartItem cartItem : cartItems) {
					Product product = productMap.get(cartItem.getProduct().getProductId());
					if (product == null) {
		                throw new RuntimeException("Product not found: " + cartItem.getProduct().getProductId());
			        }
					
					int quantity = cartItem.getQuantity();
					
					if (quantity > product.getStock()) {
					    throw new RuntimeException("Insufficient stock for product: " + product.getProductId());
					}
					
					// Deduct stock safely using an atomic operation
		            product.setStock(product.getStock() - quantity);
					
		            // Prepare OrderItem
					OrderItem orderItem = new OrderItem();
					
					BigDecimal productPrice = product.getPrice();
					BigDecimal shipping = productPrice.multiply(BigDecimal.valueOf(0.12)); // 12% of price
					BigDecimal total = productPrice.add(shipping);
					
					orderItem.setOrder(order);
					orderItem.setProduct(product.getProductId());
					orderItem.setPricePerUnit(product.getPrice());
					orderItem.setQuantity(quantity);
					orderItem.setTotalPrice(total);
					
					orderItems.add(orderItem);	
				}
				
				// Save all changes within a single transaction
		        orderRepo.save(order);
		        productRepo.saveAll(products); // Bulk update products to prevent race conditions
		        orderItemRepo.saveAll(orderItems); // Save all order items

		        // Clear cart **only after all operations succeed
		        cartItemRepo.deleteAllByUserId(userId);

		        return true;
			} else {
			return false;
			}	
	 }  catch (RazorpayException e) {
	        throw new RuntimeException("Payment verification failed", e);
	    } catch (Exception e) {
	        throw new RuntimeException("Order processing failed", e);
	    }
	}
}
