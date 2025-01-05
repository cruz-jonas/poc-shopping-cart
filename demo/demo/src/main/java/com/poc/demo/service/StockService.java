package com.poc.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.demo.dto.Product;
import com.poc.demo.dto.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductService productService;

    // Print updated stock
    public void printProducts(Stock stock) throws InterruptedException {
        Thread.sleep(1000L);
        stock.getProducts().forEach(System.out::println);
        System.out.println();
    }

    // Load stock from a JSON file
    public void loadStock(String filePath, Stock stock) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            stock.setProducts(objectMapper.readValue(new File(filePath), new TypeReference<>() {}));
            System.out.println("### Stock successfully loaded from " + filePath);
        } catch (IOException e) {
            System.err.println("*** Error loading Stock from file: " + e.getMessage());
        }
    }


    // Update stock quantity of a product
    public boolean updateStock(Stock stock, String name, int quantity) {
        Product product = productService.findProductByName(stock.getProducts(), name);
        if (product != null) {
            int currentStock = product.getStockQuantity();
            if (currentStock >= quantity) {
                product.setStockQuantity(currentStock - quantity);
                return true;
            } else {
                System.out.println("*** Not enough stock available for " + name);
            }
        } else {
            System.out.println("*** Product not found: " + name);
        }
        return false;
    }

}
