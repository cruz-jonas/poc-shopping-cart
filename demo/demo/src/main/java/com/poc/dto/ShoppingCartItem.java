package com.poc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartItem extends StockProduct {

    private Double total;

    public Double getTotal() {
        total = super.getQuantity() * super.getPrice();
        return total;
    }

    @Override
    public String toString() {
        return super.getName() + " - " + super.getQuantity() + "un - Unit Price €" + super.getPrice() + " - Total Item €" + getTotal();
    }

}
