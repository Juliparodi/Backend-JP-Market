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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public String placeOrder(OrderRequestDTO orderRequest) {

        Order order = orderService.createOrder(orderRequest);
        List<String> skuCodes = orderService.extractSkuCodes(order);

        long startTime = System.currentTimeMillis();

        try {
            inventoryService.processAndValidateStock(skuCodes);

            log.debug("Call to inventory took: {} ms", System.currentTimeMillis() - startTime);

            if (System.currentTimeMillis() - startTime >= 4000) {
                throw new TimeoutException("Inventory timeout");
            }

            orderService.saveOrder(order);
            log.debug("Order saved");

            sendEvent(order);

            log.debug("Order placer succesfully");

            return "Order placed successfully";

        } catch (TimeoutException e) {
            log.error("Timeout while placing order {}", order.getOrderNumber(), e);
            throw new RuntimeException("Timeout while placing order", e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    private void sendEvent(Order order) {

        OrderPlacedEvent event = new OrderPlacedEvent(order.getOrderNumber());

        kafkaTemplate.send("orderTopic", event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka publish failed for order {}", order.getOrderNumber(), ex);
                        // optional: store to DB / retry / DLQ
                    }
                });
    }
}
