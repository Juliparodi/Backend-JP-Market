package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;

public interface IOrderService {

    void placeOrder(OrderRequestDTO orderRequest);
}
