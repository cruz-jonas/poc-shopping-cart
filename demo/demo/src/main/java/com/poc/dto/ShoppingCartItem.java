package com.poc.dto;

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

    public Double getTotal() {
        total = quantity * price;
        return total;
    }

    @Override
    public String toString() {
        return name + " - " + quantity + "un - Unit Price €" + price + " - Total Item €" + getTotal();
    }

}
