package com.poc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Stock {

    private List<Product> products;

    // Add a new product to inventory
    public void addProduct(Product product) {
        this.products.add(product);
    }

}
