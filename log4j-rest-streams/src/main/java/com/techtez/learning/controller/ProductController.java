package com.techtez.learning.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtez.learning.model.Product;
import com.techtez.learning.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	private static final Logger logger = LogManager.getLogger(ProductController.class);
	
	private final ProductService productService;
	
	public ProductController(ProductService productService) {
        this.productService = productService;
    }
	
	@GetMapping
	public List<Product> getAllProducts(@RequestParam(value = "minPrice", required = false) Double minPrice)
	{
		if(minPrice != null)
		{
			logger.info("Fetching products with minPrice {}", minPrice);
			return productService.getProductsByMinPrice(minPrice);
		}
		else 
		{
			logger.info("Fetching all products");
			return productService.getAllProducts();
		}
	}
	
	@GetMapping("/{id}")
	public Product getProductById(@PathVariable String id)
	{
		logger.info("Fetching product by ID: {}",id);
		return productService.getProductById(id);
	}
	
	@PostMapping
	public String addProduct(@RequestBody Product product)
	{
		logger.info("Adding new product: {}", product);
		return "Product added successfully";
	}
	
	@GetMapping("/total")
	public double getTotalValue()
	{
		logger.info("Calculating total inventory value");
		return productService.getTotalValue();
	}
}
