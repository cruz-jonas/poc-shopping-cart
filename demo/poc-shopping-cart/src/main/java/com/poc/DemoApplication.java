package com.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.dto.ShoppingCart;
import com.poc.dto.Stock;
import com.poc.enumeration.OperationEnum;
import com.poc.service.ShoppingCartService;
import com.poc.service.StockService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws InterruptedException, IOException {
        SpringApplication.run(DemoApplication.class, args);
        Thread.sleep(1000L);
        ObjectMapper objectMapper = new ObjectMapper();
        StockService stockService = new StockService(objectMapper);
        ShoppingCartService shoppingCartService = new ShoppingCartService();
        ShoppingCart cart = new ShoppingCart(new ArrayList<>(), 0.0);
        Scanner scanner = new Scanner(System.in);
        Stock stock = stockService.loadStock();

        System.out.println();
        System.out.println("### Welcome to the Shopping Cart Program, follow the steps below!");
        System.out.println();
        while (true) {
            System.out.println("### Let's go, choose an option");
            System.out.println("1. List available products");
            System.out.println("2. Add product to cart");
            System.out.println("3. Remove product from cart");
            System.out.println("4. View your cart");
            System.out.println("5. Finish your purchase and generate JSON)");
            System.out.println("6. Exit program");
            System.out.print("Type here: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.println("### Available products:");
                    System.out.println();
                    stockService.printProducts(stock);
                }
                case 2 -> {
                    System.out.println();
                    System.out.print("Enter the product name to add: ");
                    String productName = scanner.nextLine();
                    System.out.print("Enter the quantity: ");
                    int quantity = scanner.nextInt();
                    System.out.println();

                    if (shoppingCartService.insertItemToCart(stock, productName, quantity, cart)) {
                        stockService.updateStock(stock, productName, quantity, OperationEnum.PLUS);
                    }
                }
                case 3 -> {
                    System.out.println();
                    System.out.print("Enter the product name to remove: ");
                    String productName = scanner.nextLine();
                    System.out.print("Enter the quantity: ");
                    int quantity = scanner.nextInt();
                    System.out.println();

                    if(shoppingCartService.removeItemFromCart(productName, quantity, cart)) {
                        stockService.updateStock(stock, productName, quantity, OperationEnum.MINUS);
                    }
                }
                case 4 -> shoppingCartService.printCart(cart);
                case 5 -> {
                    if(shoppingCartService.saveToJson(cart)) {
                        cart = new ShoppingCart(new ArrayList<>(), 0.0);
                    }
                }
                case 6 -> {
                    cart.getItems()
                            .forEach(item -> stockService.updateStock(stock, item.getName(), item.getQuantity(), OperationEnum.MINUS));
                    System.out.println("### Exiting... Thank you!");
                    return;
                }
                default -> System.out.println("*** Invalid option.");
            }
        }
    }

}
