package com.poc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Product {

    private String name;
    private double price;
    private int stockQuantity;
    private String category;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", category='" + category + '\'' +
                '}';
    }

}