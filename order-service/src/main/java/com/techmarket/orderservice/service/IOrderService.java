package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;

import java.util.List;

public interface IOrderService {

    Order createOrder(OrderRequestDTO orderRequest);
    String saveOrder(Order order);
    List<String> extractSkuCodes(Order order);
}
