package com.techmarket.orderservice.service.impl;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.event.OrderPlacedEvent;
import com.techmarket.orderservice.service.InventoryService;
import com.techmarket.orderservice.service.IOrderProccessingService;
import com.techmarket.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OrderProcessingServiceImpl implements IOrderProccessingService {

    private final IOrderService orderService;
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Override
    public String placeOrder(OrderRequestDTO orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        List<String> skuCodes = orderService.extractSkuCodes(order);

        long startTime = System.currentTimeMillis();
        try {
            inventoryService.processAndValidateStock(skuCodes);
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
