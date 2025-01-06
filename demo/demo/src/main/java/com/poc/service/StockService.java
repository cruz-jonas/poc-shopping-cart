package com.poc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.dto.Product;
import com.poc.dto.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final String STOCK_PATH = "demo/src/main/java/com/poc/repository/stock.json";
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    // Print updated stock
    public void printProducts(Stock stock) throws InterruptedException {
        Thread.sleep(1000L);
        stock.getProducts().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .forEach(System.out::println);
        System.out.println();
    }

    // Load stock from a JSON file
    public Stock loadStock() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Stock stock = new Stock(new ArrayList<>());
        try {
            stock.setProducts(objectMapper.readValue(new File(STOCK_PATH), new TypeReference<>() {}));
            return stock;
        } catch (IOException e) {
            throw new IOException("*** Error loading Stock from file: " + e.getMessage());
        }
    }


    // Update stock quantity of a product
    public boolean updateStock(Stock stock, String name, int quantity, boolean removed) {
        Product product = productService.findProductByName(stock.getProducts(), name);
        if (product != null) {
            int currentStock = product.getStockQuantity();
            if (removed) {
                product.setStockQuantity(currentStock + quantity);
                return true;
            } else if (currentStock >= quantity) {
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

    // Update stock in genereal
    public void updateStock(List<Product> products) throws IOException {
        objectMapper.writeValue(new File(STOCK_PATH), products);
    }

    //
    public void insertOrUpdate(Product newProduct) throws IOException {

        List<Product> products = loadStock().getProducts();

        Optional<Product> existingProduct = products.stream()
                .filter(product -> product.getName().equals(newProduct.getName()))
                .findFirst();

        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setStockQuantity(product.getStockQuantity() + newProduct.getStockQuantity());
        } else {
            products.add(newProduct);
        }

       updateStock(products);


    }

    //
    public void remove(String name) throws IOException {

        List<Product> products = loadStock().getProducts();

        boolean removed = products.removeIf(product -> product.getName().equalsIgnoreCase(name));

        if (!removed) {
            //product not found
        }

        updateStock(products);

    }

}
