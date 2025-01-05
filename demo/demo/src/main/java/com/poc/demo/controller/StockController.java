package com.poc.demo.controller;

import com.poc.demo.controller.model.request.ProductRequest;
import com.poc.demo.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products Stock Endpoints")
@RestController
@RequestMapping(path = "/v1")
@RequiredArgsConstructor
public class StockController {

    @Value("${token}")
    private String token;
    private final StockService stockService;

    @Operation(
            method = "POST",
            summary = "Register product"
    )
    @PostMapping()
    public ResponseEntity<HttpStatus> registerProduct(@RequestHeader(value = "Authorization") String token,
                                                          @Valid @RequestBody ProductRequest productRequest) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
