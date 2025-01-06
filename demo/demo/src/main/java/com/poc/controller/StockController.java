package com.poc.controller;

import com.poc.dto.Product;
import com.poc.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Products Stock Endpoints")
@RestController
@RequestMapping(path = "/v1")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(
            method = "POST",
            summary = "Register product"
    )
    @PostMapping()
    public ResponseEntity<HttpStatus> registerProduct(@RequestBody Product productRequest) throws IOException {

        stockService.insertOrUpdate(productRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            method = "POST",
            summary = "Remove product"
    )
    @PostMapping("/remove/{name}")
    public ResponseEntity<HttpStatus> removeProduct(@PathVariable String name) throws IOException {

        stockService.remove(name);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
