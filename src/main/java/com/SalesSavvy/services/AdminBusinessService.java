package com.SalesSavvy.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.SalesSavvy.entities.Order;
import com.SalesSavvy.entities.OrderItem;
import com.SalesSavvy.repositories.OrderRepository;
import com.SalesSavvy.repositories.ProductRepository;

@Service
public class AdminBusinessService {

	private final OrderRepository orderRepo;
	private final ProductRepository productRepo;

	public AdminBusinessService(OrderRepository orderRepo, ProductRepository productRepo) {
		super();
		this.orderRepo = orderRepo;
		this.productRepo = productRepo;
		
	}
	
	public Map<String, Object> getOverallBusiness() {
		return calulateBusinessMetrics(orderRepo.getAllSuccessfulOrders());
	}

	public Map<String, Object> getMonthlyBusiness(int month, int year) {
		return calulateBusinessMetrics(orderRepo.getSuccessfulOrdersBymonth(month, year));
	}
	
	public Map<String, Object> getYearlyBusiness(int year) {
		return calulateBusinessMetrics(orderRepo.getSuccessfulOrdersByYear(year));
	}
	
	public Map<String, Object> getDayBusiness(LocalDate date) {
		return calulateBusinessMetrics(orderRepo.getSuccessfulOrdersByDate(date));
	}
	
	
	public Map<String, Object> calulateBusinessMetrics(List<Order> orders){
		// For calculating no of items sold in each category
		Map<String, Integer> categorySale = new HashMap<>();
		double totalRevenue = 0.0;
		
		for(Order order : orders) {
			totalRevenue += order.getTotalAmount().doubleValue();
			for(OrderItem orderItem : order.getOrderItems()) {
				
				String categoryName = productRepo.findCategoryNameByProductId(orderItem.getProductId());				
				categorySale.put(categoryName, (categorySale.getOrDefault(categoryName, 0) + orderItem.getQuantity()));
			}
		}
		
		return Map.of("totalRevenue", totalRevenue, "categorySale", categorySale);
	}
}
