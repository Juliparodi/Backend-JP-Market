package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;

public interface IOrderProccessingService {
    String placeOrder(OrderRequestDTO orderRequest);

}
