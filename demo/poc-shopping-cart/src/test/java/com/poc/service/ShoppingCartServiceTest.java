package com.poc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.dto.ShoppingCart;
import com.poc.dto.ShoppingCartItem;
import com.poc.dto.Stock;
import com.poc.dto.StockProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private Stock stock;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() throws IOException {
        // load stockTest.json file and initialize a cart
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/stockTest.json");
        stock = objectMapper.readValue(inputStream, Stock.class);
        cart = new ShoppingCart();
        cart.setItems(new ArrayList<>());
        cart.setTotal(0.0);
    }

    @Test
    void insertItemToCartProductNotFoundTest() {
        boolean result = shoppingCartService.insertItemToCart(stock, "ProductNotInStock", 2, cart);
        
        assertFalse(result);
        assertEquals(0, cart.getItems().size());
        assertEquals(0.0, cart.getTotal());
    }

    @Test
    void removeItemFromCartSuccessTest() {
        StockProduct stockProduct = stock.getProducts().get(0);
        ShoppingCartItem item = new ShoppingCartItem();
        item.setName(stockProduct.getName());
        item.setQuantity(stockProduct.getQuantity());
        item.setPrice(stockProduct.getPrice());
        cart.getItems().add(item);
        cart.setTotal(item.getQuantity()*item.getPrice());

        boolean result = shoppingCartService.removeItemFromCart(stockProduct.getName(), 1, cart);

        assertTrue(result);
        assertEquals(1, cart.getItems().size());
        assertEquals(9, cart.getItems().get(0).getQuantity());
        assertEquals(0.0, cart.getTotal());
    }

    @Test
    void removeItemProductNotInCartTest() {
        boolean result = shoppingCartService.removeItemFromCart("ProductNotInCart", 1, cart);
        
        assertFalse(result);
        assertEquals(0, cart.getItems().size());
        assertEquals(0.0, cart.getTotal());
    }

    @Test
    void insertItemToCartSuccessTest() {
        boolean result = shoppingCartService.insertItemToCart(stock, "Charger", 2, cart);

        assertTrue(result);
        assertEquals(1, cart.getItems().size());
        assertEquals("Charger", cart.getItems().get(0).getName());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(5, cart.getTotal());
    }

    @Test
    void saveCartToJsonSuccess() {
        StockProduct stockProduct = stock.getProducts().get(1);
        ShoppingCartItem item = new ShoppingCartItem();
        item.setName(stockProduct.getName());
        item.setQuantity(stockProduct.getQuantity());
        item.setPrice(stockProduct.getPrice());
        cart.getItems().add(item);
        cart.setTotal(item.getQuantity()*item.getPrice());
        
        boolean result = shoppingCartService.saveToJson(cart);

        assertTrue(result);
    }
}
