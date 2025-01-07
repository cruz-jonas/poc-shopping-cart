package com.poc.controller.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Mandatory to inform name")
    private String name;
    @NotNull(message = "Mandatory to inform price")
    private double price;
    private int stockQuantity;
    private String category;

}
