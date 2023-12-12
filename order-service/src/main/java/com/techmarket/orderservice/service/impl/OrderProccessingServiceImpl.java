package com.techmarket.orderservice.service.impl;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.service.IInventoryService;
import com.techmarket.orderservice.service.IOrderProccessingService;
import com.techmarket.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OrderProccessingServiceImpl implements IOrderProccessingService {

    private final IOrderService orderService;
    private final IInventoryService inventoryService;

    @Override
    public String placeOrder(OrderRequestDTO orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        List<String> skuCodes = orderService.extractSkuCodes(order);

        long startTime = System.currentTimeMillis();
        try {
            inventoryService.proccesAndValidateStock(skuCodes);
            if (System.currentTimeMillis() - startTime >= 4000) {
                throw new TimeoutException();
            } else {
                return orderService.saveOrder(order);
            }
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
