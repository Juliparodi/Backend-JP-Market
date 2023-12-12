package com.techmarket.orderservice.controller;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.service.IOrderProccessingService;
import com.techmarket.orderservice.service.IOrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/techMarket/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderProccessingService orderProccessingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody @Valid OrderRequestDTO orderRequest) {
        return CompletableFuture.supplyAsync(() -> orderProccessingService.placeOrder(orderRequest));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequestDTO orderRequest,
                                            WebClientResponseException webClientResponseException) {
        return CompletableFuture.supplyAsync(() -> "holi, circuit breaker open");
    }

}