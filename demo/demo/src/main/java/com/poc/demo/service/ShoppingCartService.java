package com.poc.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.demo.dto.Product;
import com.poc.demo.dto.ShoppingCart;
import com.poc.demo.dto.ShoppingCartItem;
import com.poc.demo.dto.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final StockService stockService;
    private final ProductService productService;

    public void insertItem(Stock stock, String productName, int quantity, ShoppingCart cart) {
        if (stockService.updateStock(stock, productName, quantity)) {
            List<Product> products = stock.getProducts();
            Product stockProduct = productService.findProductByName(products, productName);
            if (Objects.nonNull(stockProduct)) {
                ShoppingCartItem addedProduct = new ShoppingCartItem();
                addedProduct.setIdProduct(stockProduct.getId());
                addedProduct.setName(stockProduct.getName());
                addedProduct.setPrice(stockProduct.getPrice());
                addedProduct.setQuantity(quantity);
                addedProduct.setTotal(addedProduct.getQuantity() * stockProduct.getPrice());
                addItem(cart, addedProduct);
                cart.setTotal(cart.getTotal() + addedProduct.getTotal());
                System.out.println("### Product added to the cart.");
                System.out.println();
            }
        } else {
            System.out.println("*** Product not found or insufficient stock.");
            System.out.println();
        }
    }

    public void printCart(ShoppingCart shoppingCart) {
        System.out.println();
        System.out.println("### Here is your Shopping Cart:");
        System.out.println();
        shoppingCart.getItems().forEach(System.out::println);
        System.out.println("Total: €" + shoppingCart.getTotal());
        System.out.println();
    }

    public void saveToJson(ShoppingCart cart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("shoppingCart.json"), cart);
            System.out.println();
            System.out.println("### Cart saved as 'shoppingCart.json'.");
            System.out.println();

        } catch (IOException e) {
            System.out.println();
            System.err.println("*** Error saving the cart: " + e.getMessage());
            System.out.println();
        }
    }

    public void addItem(ShoppingCart cart, ShoppingCartItem item) {
        cart.getItems().stream()
                .filter(streamedItem -> Objects.equals(item.getIdProduct(), streamedItem.getIdProduct()))
                .findFirst()
                .ifPresentOrElse(streamedItem -> {
                            streamedItem.setQuantity(streamedItem.getQuantity() + item.getQuantity());
                            streamedItem.setTotal(streamedItem.getTotal() + item.getTotal());
                        },
                        () -> cart.getItems().add(item));
    }

}
