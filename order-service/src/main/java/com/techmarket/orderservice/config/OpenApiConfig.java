package com.techmarket.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventoy Service JP Market")
                        .version("1.0.0")
                        .description("Inventory Service API Documentation")
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local and Docker")
                ));
    }
}
