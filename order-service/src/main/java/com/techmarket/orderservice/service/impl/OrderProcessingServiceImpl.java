package com.techmarket.orderservice.service.impl;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.event.OrderPlacedEvent;
import com.techmarket.orderservice.service.InventoryService;
import com.techmarket.orderservice.service.IOrderProcessingService;
import com.techmarket.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderProcessingServiceImpl implements IOrderProcessingService {

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
            log.debug("Call to inventory took: {} ms", System.currentTimeMillis() - startTime);
            if (System.currentTimeMillis() - startTime >= 4000) {
                throw new TimeoutException();
            } else {
                log.debug("saving order..");
                orderService.saveOrder(order);
                log.debug("sending event..");
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
