package com.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.dto.ShoppingCart;
import com.poc.service.ProductService;
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
        ProductService productService = new ProductService();
        ObjectMapper objectMapper = new ObjectMapper();
        StockService stockService = new StockService(productService, objectMapper);
        ShoppingCartService shoppingCartService = new ShoppingCartService(stockService, productService);
        ShoppingCart cart = new ShoppingCart(new ArrayList<>(), 0.0);
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("### Welcome to the Shopping Cart Program!");
        System.out.println();
        while (true) {
            System.out.println("### Choose an option");
            System.out.println("1. List products");
            System.out.println("2. Add product to cart");
            System.out.println("3. Remove product from cart");
            System.out.println("4. View cart");
            System.out.println("5. Complete purchase (Generate JSON)");
            System.out.println("6. Exit program");
            System.out.print("Type here: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.println("### Available products:");
                    System.out.println();
                    stockService.printProducts(stockService.loadStock());
                }
                case 2 -> {
                    System.out.println();
                    System.out.print("Enter the product name: ");
                    String productName = scanner.nextLine();
                    System.out.print("Enter the quantity: ");
                    int quantity = scanner.nextInt();
                    System.out.println();

                    shoppingCartService.insertItem(productName, quantity, cart);
                }
                case 3 -> {
                    System.out.println();
                    System.out.print("Enter the product name: ");
                    String productName = scanner.nextLine();
                    System.out.print("Enter the quantity: ");
                    int quantity = scanner.nextInt();
                    System.out.println();

                    shoppingCartService.removeItemFromCart(productName, quantity, cart);
                }
                case 4 -> shoppingCartService.printCart(cart);
                case 5 -> shoppingCartService.saveToJson(cart);
                case 6 -> {
                    System.out.println("### Exiting... Thank you!");
                    return;
                }
                default -> System.out.println("*** Invalid option.");
            }
        }
    }

}
