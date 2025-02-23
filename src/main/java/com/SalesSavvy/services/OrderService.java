package com.SalesSavvy.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.SalesSavvy.dtos.OrderDTO;
import com.SalesSavvy.entities.Order;
import com.SalesSavvy.entities.OrderItem;
import com.SalesSavvy.entities.Product;
import com.SalesSavvy.repositories.OrderRepository;
import com.SalesSavvy.repositories.ProductImageRepository;
import com.SalesSavvy.repositories.ProductRepository;

@Service
public class OrderService {
	
	private final OrderRepository orderRepo;
	private final ProductRepository prodRepo;
	private final ProductImageRepository prodImgrepo;
	
	public OrderService(OrderRepository orderRepo, ProductRepository prodRepo, ProductImageRepository prodImgrepo) {
		super();
		this.orderRepo = orderRepo;
		this.prodRepo = prodRepo;
		this.prodImgrepo = prodImgrepo;
	}

	public List<OrderDTO> getAllSuccessfulOrder(int userId){
		
		List<Order> orders =  orderRepo.getSuccessfulOrdersByUserId(userId);
		
		List<OrderDTO> orderDtos = new ArrayList<>();
		for(Order order: orders) {
			for(OrderItem item : order.getOrderItems()) {
				OrderDTO orderDto = new OrderDTO();
				orderDto.setOrderId(order.getOrderId());
				orderDto.setWithOrderItem(item);
				orderDto.setTime(order.getUpdatedAt());
				// fetching product details using produtId
				Optional<Product> productOp = prodRepo.findById(item.getProductId());
				
				if(productOp.isPresent()) {
					Product product = productOp.get();
					orderDto.setWithProduct(product);
					
					// Adding product image URL
					String productImageUrl = prodImgrepo.getUrlByProductId(product.getProductId());
					orderDto.setImgUrl(productImageUrl);
					
					orderDtos.add(orderDto);
				}
			}
		}
		return orderDtos;
	}

}
