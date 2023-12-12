package com.techmarket.orderservice.service.impl;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.event.OrderPlacedEvent;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.service.IInventoryService;
import com.techmarket.orderservice.service.IOrderProccessingService;
import com.techmarket.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OrderProccessingServiceImpl implements IOrderProccessingService {

    private final IOrderService orderService;
    private final IInventoryService inventoryService;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

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
                orderService.saveOrder(order);
                sendEvent(order);
                return "Order placed successfully";
            }
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEvent(Order order) {
        kafkaTemplate.send("orderTopic", new OrderPlacedEvent(order.getOrderNumber()));
    }
}
