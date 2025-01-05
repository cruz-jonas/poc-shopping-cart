package com.poc.demo;

import com.poc.demo.dto.ShoppingCart;
import com.poc.demo.dto.Stock;
import com.poc.demo.service.ProductService;
import com.poc.demo.service.ShoppingCartService;
import com.poc.demo.service.StockService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Scanner;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DemoApplication.class, args);
        Thread.sleep(1000L);
        ProductService productService = new ProductService();
        StockService stockService = new StockService(productService);
        ShoppingCartService shoppingCartService = new ShoppingCartService(stockService, productService);
        Stock stock = new Stock(new ArrayList<>());
        ShoppingCart cart = new ShoppingCart(new ArrayList<>(), 0.0);
        Scanner scanner = new Scanner(System.in);


        System.out.println("### Welcome to the Shopping Cart Program!");
        System.out.println();
        System.out.println("Loading stock...");
        Thread.sleep(1000L);
        stockService.loadStock("demo/src/main/resources/stock.json", stock);
        while (true) {
            System.out.println();
            System.out.println("### Choose an option");
            System.out.println("1. List products");
            System.out.println("2. Add product to cart");
            System.out.println("3. View cart");
            System.out.println("4. Complete purchase (Generate JSON)");
            System.out.println("5. Exit");
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
                    System.out.print("Enter the product name: ");
                    String productName = scanner.nextLine();
                    System.out.print("Enter the quantity: ");
                    int quantity = scanner.nextInt();
                    System.out.println();

                    shoppingCartService.insertItem(stock, productName, quantity, cart);
                }
                case 3 -> shoppingCartService.printCart(cart);
                case 4 -> shoppingCartService.saveToJson(cart);
                case 5 -> {
                    System.out.println("### Exiting... Thank you!");
                    return;
                }
                default -> System.out.println("*** Invalid option.");
            }
        }
    }

}
