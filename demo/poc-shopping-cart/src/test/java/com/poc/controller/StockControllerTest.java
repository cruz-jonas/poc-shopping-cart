package com.poc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.controller.model.request.ProductRequest;
import com.poc.service.StockService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleIOExceptionWhenRegisteringProductTest() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Calculator");
        productRequest.setQuantity(10);
        productRequest.setPrice(2.0);

        Mockito.doThrow(new IOException("ERROR")).when(stockService).insertOrUpdate(Mockito.any(ProductRequest.class));
        
        mockMvc.perform(post("/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registerProductSuccessTest() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Cushion");
        productRequest.setQuantity(10);
        productRequest.setPrice(2.0);

        doNothing().when(stockService).insertOrUpdate(Mockito.any(ProductRequest.class));
        
        mockMvc.perform(post("/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void returnBadRequestWhenRegisterProductWithoutDataTest() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        
        mockMvc.perform(post("/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeProductSuccessTest() throws Exception {
        String productName = "Desk";

        doNothing().when(stockService).remove(productName);
        
        mockMvc.perform(post("/v1/remove/{name}", productName))
                .andExpect(status().isOk());
    }

    @Test
    void handleIOExceptionWhenRemoveProductTest() throws Exception {
        String productName = "Desk Table";

        Mockito.doThrow(new IOException("Error occurred")).when(stockService).remove(productName);
        
        mockMvc.perform(post("/v1/remove/{name}", productName))
                .andExpect(status().isInternalServerError());
    }
}