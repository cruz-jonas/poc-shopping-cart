package com.poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.dto.ShoppingCart;
import com.poc.dto.ShoppingCartItem;
import com.poc.dto.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final StockService stockService;

    // insert item to cart
    public boolean insertItemToCart(Stock stock, String productName, int quantity, ShoppingCart cart) {
        return stock.getProducts()
                .stream()
                .filter(stockProduct -> stockProduct.getName().equalsIgnoreCase(productName))
                .findFirst()
                .map(availableProduct -> {
                    ShoppingCartItem addedProduct = new ShoppingCartItem();
                    addedProduct.setName(availableProduct.getName());
                    addedProduct.setPrice(availableProduct.getPrice());
                    addedProduct.setQuantity(quantity);
                    addedProduct.setTotal(addedProduct.getQuantity() * availableProduct.getPrice());
                    addItem(cart, addedProduct);
                    cart.setTotal(cart.getTotal() + addedProduct.getTotal());
                    System.out.println("### Product added to the cart.");
                    System.out.println();
                    return true;
                })
                .orElseGet(() -> {
                    System.out.println("*** Product not found or insufficient stock.");
                    System.out.println();
                    return false;
                });
    }

    // remove item from cart
    public boolean removeItemFromCart(String productName, int quantity, ShoppingCart cart) {
        return cart.getItems().stream()
                .filter(p -> p.getName().equalsIgnoreCase(productName))
                .findFirst()
                .map(cartItem -> {
                    Double toRemoveFromTotal = cartItem.getTotal();
                    if (cartItem.getQuantity() >= quantity) {
                        cartItem.setQuantity(cartItem.getQuantity() - quantity);
                        cart.setTotal(cart.getTotal() - toRemoveFromTotal);
                        if (cartItem.getQuantity() == 0) {
                            cart.getItems().removeIf(p -> p.getName().equalsIgnoreCase(productName));
                        }
                        System.out.println("### Product quantity removed from the cart.");
                        System.out.println();
                        return true;
                    } else {
                        System.out.println("### Product quantity requested greater than available to remove.");
                        System.out.println();
                        return false;
                    }
                }).orElseGet(() -> {
                    System.out.println("### Product not found in the cart.");
                    System.out.println();
                    return false;
                });
    }

    // insert item into cart or update existing
    public void addItem(ShoppingCart cart, ShoppingCartItem item) {
        cart.getItems().stream()
                .filter(streamedItem -> Objects.equals(item.getName(), streamedItem.getName()))
                .findFirst()
                .ifPresentOrElse(streamedItem -> {
                            streamedItem.setQuantity(streamedItem.getQuantity() + item.getQuantity());
                        },
                        () -> cart.getItems().add(item));
    }

    // print updated cart
    public void printCart(ShoppingCart shoppingCart) {
        System.out.println();
        System.out.println("### Here is your Shopping Cart:");
        System.out.println();
        shoppingCart.getItems().forEach(System.out::println);
        System.out.println("Total: €" + shoppingCart.getTotal());
        System.out.println();
    }

    // save file into C:/temp
    public boolean saveToJson(ShoppingCart cart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File directory = new File("C:/temp");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "shoppingCart.json");
            mapper.writeValue(file, cart);

            System.out.println();
            System.out.println("### Cart saved as " + file.getAbsolutePath());
            System.out.println();
            return true;

        } catch (IOException e) {
            System.out.println();
            System.err.println("*** Error saving the cart: " + e.getMessage());
            System.out.println();
            return false;
        }
    }

}
