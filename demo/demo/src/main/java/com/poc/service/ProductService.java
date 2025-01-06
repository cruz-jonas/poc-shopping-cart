package com.poc.service;

import com.poc.dto.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    // Find a product by name
    public Product findProductByName(List<Product> products, String name) {
        return products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
