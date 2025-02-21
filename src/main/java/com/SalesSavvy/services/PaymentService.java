package com.SalesSavvy.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.SalesSavvy.entities.CartItem;
import com.SalesSavvy.entities.Order;
import com.SalesSavvy.entities.OrderItem;
import com.SalesSavvy.entities.OrderStatus;
import com.SalesSavvy.repositories.CartItemRepository;
import com.SalesSavvy.repositories.OrderItemRepository;
import com.SalesSavvy.repositories.OrderRepository;
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
	
	

	public PaymentService(OrderRepository orderRepo, OrderItemRepository orderItemRepo,
			CartItemRepository cartItemRepo) {
		super();
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
		this.cartItemRepo = cartItemRepo;
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
				
				// Fetch CartItems of the user
				List<CartItem> cartItems = cartItemRepo.getCartItemsByUserId(userId);
				
				// Saving CartItem 's as OrderItem 's in the OrderItem table
				
				for(CartItem cartItem : cartItems) {
					OrderItem orderItem = new OrderItem();
					
					orderItem.setOrder(order);
					orderItem.setProduct(cartItem.getProduct().getProductId());
					orderItem.setPricePerUnit(cartItem.getProduct().getPrice());
					orderItem.setQuantity(cartItem.getQuantity());
					orderItem.setTotalPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
					orderItemRepo.save(orderItem);
				}
				
				//clears the cart
				cartItemRepo.deleteAllByUserId(userId);
				return true;
			} else {
			return false;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
