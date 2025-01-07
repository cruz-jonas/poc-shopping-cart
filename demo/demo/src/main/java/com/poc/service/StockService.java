package com.poc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.controller.model.request.ProductRequest;
import com.poc.dto.Product;
import com.poc.dto.Stock;
import com.poc.enumeration.OperationEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final String STOCK_PATH = "demo/src/main/java/com/poc/repository/stock.json";
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    // print updated stock from file
    public void printProducts() throws InterruptedException, IOException {
        Stock stock = loadStock();
        Thread.sleep(1000L);
        stock.getProducts().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .forEach(System.out::println);
        System.out.println();
    }

    // load stock from a file
    public Stock loadStock() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Stock stock = new Stock(new ArrayList<>());
        try {
            stock.setProducts(objectMapper.readValue(new File(STOCK_PATH), new TypeReference<>() {
            }));
            return stock;
        } catch (IOException e) {
            throw new IOException("*** Error loading Stock from file: " + e.getMessage());
        }
    }


    // update stock quantity of a product when insert or remove from cart
    public boolean updateStock(Stock stock, String name, int quantity, OperationEnum operationEnum) throws IOException {
        Product product = productService.findProductByName(stock.getProducts(), name);
        if (product != null) {
            int currentStock = product.getStockQuantity();
            if (operationEnum.getOperation().equals("MINUS")) {
                product.setStockQuantity(currentStock + quantity);
                updateStock(stock.getProducts());
                return true;
            } else if (currentStock >= quantity) {
                product.setStockQuantity(currentStock - quantity);
                updateStock(stock.getProducts());
                return true;
            } else {
                System.out.println("*** Not enough stock available for " + name);
            }
        } else {
            System.out.println("*** Product not found: " + name);
        }
        return false;
    }

    // Update stock file
    public void updateStock(List<Product> products) throws IOException {
        try {
            objectMapper.writeValue(new File(STOCK_PATH), products);
        } catch(IOException e) {
            throw new IOException("*** Error updating Stock to file: " + e.getMessage());
        }
    }

    // insert product in stock file if new or update if existing
    public void insertOrUpdate(ProductRequest newProduct) throws IOException {
        List<Product> products = loadStock().getProducts();

        Optional<Product> existingProduct = products.stream()
                .filter(product -> product.getName().equals(newProduct.getName()))
                .findFirst();

        Product product;
        if (existingProduct.isPresent()) {
            product = existingProduct.get();
            product.setPrice(verifyPrice(newProduct, product));
            product.setStockQuantity(product.getStockQuantity() + newProduct.getStockQuantity());
        } else {
            product = new Product();
            product.setName(newProduct.getName());
            product.setStockQuantity(newProduct.getStockQuantity());
            product.setPrice(newProduct.getPrice());
            product.setCategory(Objects.nonNull(newProduct.getCategory()) ? newProduct.getCategory() : "Undefined Category");
            products.add(product);
        }
        updateStock(products);
    }

    // verify if price different
    private Double verifyPrice(ProductRequest newProduct, Product product) {
        if (newProduct.getPrice() != product.getPrice()) {
            System.out.println("Different prices - the previous price will be maintained - please contact management.");
        }
        return product.getPrice();
    }

    // remove product if exist
    public void remove(String name) throws IOException {
        List<Product> products = loadStock().getProducts();
        products.removeIf(product -> product.getName().equalsIgnoreCase(name));
        updateStock(products);

    }

}
