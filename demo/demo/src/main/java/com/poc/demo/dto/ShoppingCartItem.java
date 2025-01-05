package com.poc.demo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ShoppingCartItem {

    private String name;
    private double price;
    private int quantity;
    private Double total;
    private Long idProduct;


    @Override
    public String toString() {
        return name + " - " + quantity + "un" + " - €" + total;
    }

}
