package com.poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.dto.Product;
import com.poc.dto.ShoppingCart;
import com.poc.dto.ShoppingCartItem;
import com.poc.dto.Stock;
import com.poc.enumeration.OperationEnum;
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

    // insert item to cart and update stock file
    public void insertItem(String productName, int quantity, ShoppingCart cart) throws IOException {
        Stock stock = stockService.loadStock();
        if (stockService.updateStock(stock, productName, quantity, OperationEnum.PLUS)) {
            List<Product> products = stock.getProducts();
            Product stockProduct = productService.findProductByName(products, productName);
            if (Objects.nonNull(stockProduct)) {
                ShoppingCartItem addedProduct = new ShoppingCartItem();
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

    // remove item from cart and update stock file
    public void removeItemFromCart(String productName, int quantity, ShoppingCart cart) throws IOException {
        Stock stock = stockService.loadStock();
        if(stockService.updateStock(stock, productName, quantity, OperationEnum.MINUS)) {
            cart.getItems().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(productName))
                    .findFirst()
                    .ifPresent(cartItem -> {
                        Double toRemoveFromTotal = cartItem.getTotal();
                        if (cartItem.getQuantity() >= quantity) {
                            cartItem.setQuantity(cartItem.getQuantity() - quantity);
                            cart.setTotal(cart.getTotal() - toRemoveFromTotal);
                            if (cartItem.getQuantity() == 0) {
                                cart.getItems().removeIf(p -> p.getName().equalsIgnoreCase(productName));
                            }
                            System.out.println("### Product quantity removed from the cart.");
                            System.out.println();
                        } else {
                            System.out.println("### Product quantity requested greater than available to remove.");
                            System.out.println();
                        }
                    });
        } else {
            System.out.println("### Product not found to remove.");
            System.out.println();
        }
    }

    // insert item into cart or update existing
    public void addItem(ShoppingCart cart, ShoppingCartItem item) {
        cart.getItems().stream()
                .filter(streamedItem -> Objects.equals(item.getName(), streamedItem.getName()))
                .findFirst()
                .ifPresentOrElse(streamedItem -> {
                            streamedItem.setQuantity(streamedItem.getQuantity() + item.getQuantity());
                            streamedItem.setTotal(streamedItem.getTotal() + item.getTotal());
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
    public void saveToJson(ShoppingCart cart) {
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

        } catch (IOException e) {
            System.out.println();
            System.err.println("*** Error saving the cart: " + e.getMessage());
            System.out.println();
        }
    }

}
