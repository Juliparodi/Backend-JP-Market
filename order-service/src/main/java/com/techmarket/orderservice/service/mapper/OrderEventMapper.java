package com.techmarket.orderservice.service.mapper;

import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.entities.OrderLineItems;
import com.techmarket.orderservice.domain.event.OrderItemEvent;
import com.techmarket.orderservice.domain.event.OrderPlacedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderEventMapper {

    public OrderPlacedEvent toOrderPlacedEvent(Order order) {
        List<OrderItemEvent> items = order.getOrderLineItemsList()
                .stream()
                .map(this::toOrderItemEvent)
                .toList();

        return new OrderPlacedEvent(
                UUID.randomUUID(),
                order.getOrderId(),
                order.getOrderNumber(),
                order.getCreatedDate(),
                items
        );
    }

    public OrderItemEvent toOrderItemEvent(OrderLineItems item) {
        return new OrderItemEvent(
                item.getSkuCode(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
