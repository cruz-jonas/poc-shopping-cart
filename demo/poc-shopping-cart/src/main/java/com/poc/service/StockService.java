package com.poc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.controller.model.request.ProductRequest;
import com.poc.dto.StockProduct;
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

    private static final String STOCK_PATH = "poc-shopping-cart/src/main/java/com/poc/repository/stock.json";
    private final ObjectMapper objectMapper;

    // print updated stock from file
    public void printProducts(Stock stock) throws InterruptedException {
        Thread.sleep(1000L);
        stock.getProducts().stream()
                .filter(p -> p.getQuantity() > 0)
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


    // update stock when insert or remove from cart
    public void updateStock(Stock stock, String productName, int quantity, OperationEnum operationEnum) {
        stock.getProducts().stream()
                .filter(stockProduct -> stockProduct.getName().equalsIgnoreCase(productName))
                .findFirst()
                .ifPresentOrElse(availableProduct -> {
                    int currentStock = availableProduct.getQuantity();
                    if (operationEnum.getOperation().equals("MINUS")) {
                        availableProduct.setQuantity(currentStock + quantity);
                        updateStock(stock.getProducts());
                    } else if (currentStock >= quantity) {
                        availableProduct.setQuantity(currentStock - quantity);
                        updateStock(stock.getProducts());
                    } else {
                        System.out.println("*** Not enough stock available for " + productName);
                    }
                }, () -> System.out.println("*** Product not found: " + productName));
    }

    // Update stock file
    private void updateStock(List<StockProduct> products) {
        try {
            objectMapper.writeValue(new File(STOCK_PATH), products);
        } catch(IOException e) {
            System.out.println("*** Error updating Stock to file: " + e.getMessage());
        }
    }

    // insert product in stock file if new or update if existing
    public void insertOrUpdate(ProductRequest newProduct) throws IOException {
        List<StockProduct> products = loadStock().getProducts();

        Optional<StockProduct> existingProduct = products.stream()
                .filter(product -> product.getName().equals(newProduct.getName()))
                .findFirst();

        StockProduct product;
        if (existingProduct.isPresent()) {
            product = existingProduct.get();
            product.setPrice(verifyPrice(newProduct, product));
            product.setQuantity(product.getQuantity() + newProduct.getQuantity());
        } else {
            product = new StockProduct();
            product.setName(newProduct.getName());
            product.setQuantity(newProduct.getQuantity());
            product.setPrice(newProduct.getPrice());
            product.setCategory(Objects.nonNull(newProduct.getCategory()) ? newProduct.getCategory() : "Undefined Category");
            products.add(product);
        }
        updateStock(products);
    }

    // remove product if exist
    public void remove(String name) throws IOException {
        List<StockProduct> products = loadStock().getProducts();
        products.removeIf(product -> product.getName().equalsIgnoreCase(name));
        updateStock(products);

    }

    // verify if price different
    private Double verifyPrice(ProductRequest newProduct, StockProduct product) {
        if (newProduct.getPrice() != product.getPrice()) {
            System.out.println("Different prices - the previous price will be maintained - please contact management.");
        }
        return product.getPrice();
    }

}
