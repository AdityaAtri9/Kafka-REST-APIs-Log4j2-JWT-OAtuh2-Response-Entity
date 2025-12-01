package com.techtez.learning.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.techtez.learning.model.Product;

@Service
public class ProductService {

	private static final Logger logger = LogManager.getLogger(ProductService.class);
	
	private final List<Product> products = new ArrayList<>();
	
	public ProductService()
	{
		 // Sample data
        products.add(new Product("p1", "Laptop", 1000));
        products.add(new Product("p2", "Mouse", 25));
        products.add(new Product("p3", "Keyboard", 45));
        products.add(new Product("p4", "Monitor", 200));
	}
	
	public List<Product> getAllProducts()
	{
		logger.info("Fetching all products, total count: {}", products.size());
		return products;
	}
	
	public List<Product> getProductsByMinPrice(double minPrice)
	{
		List<Product> filtered = products.stream()
				.filter(p -> p.getPrice() >= minPrice)
				.collect(Collectors.toList());
		
		logger.debug("Products filtered by minPrice {}: {}", minPrice, filtered.size());
		return filtered;
	}
	
	public Product getProductById(String id)
	{
		logger.debug("Fetching product by ID: {}", id);
		return products.stream()
				.filter(p -> p.getId().equals(id))
				.findFirst()
				.orElse(null);
	}
	
	public void addProduct(Product product)
	{
		products.add(product);
		logger.debug("Added product: {}", product);
	}
	
	public double getTotalValue()
	{
		double total = products.stream()
				.mapToDouble(Product :: getPrice)
				.sum();
		
		logger.debug("Total inventory value calculated: {}", total);
		return total;
	}
}








