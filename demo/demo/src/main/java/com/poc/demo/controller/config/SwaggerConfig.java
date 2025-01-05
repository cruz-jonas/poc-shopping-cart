package com.poc.demo.controller.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                description = "Responsible service for manage product stock",
                version = "1.0.0",
                title = "Product Stock Manager"
        )
)
public class SwaggerConfig {
}
