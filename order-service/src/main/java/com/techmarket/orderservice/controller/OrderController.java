package com.techmarket.orderservice.controller;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/techMarket/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }
}