package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;

public interface IOrderProcessingService {
    String placeOrder(OrderRequestDTO orderRequest);

}
