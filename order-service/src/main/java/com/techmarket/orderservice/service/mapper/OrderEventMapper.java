package com.techmarket.orderservice.service.mapper;

import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.entities.OrderLineItems;
import com.techmarket.schema.OrderItemEvent;
import com.techmarket.schema.OrderPlacedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderEventMapper {

    public OrderPlacedEvent toOrderPlacedEvent(Order order) {
        List<OrderItemEvent> items = order.getOrderLineItemsList().stream()
                .map(this::toOrderItemEvent)
                .collect(Collectors.toList());

        return OrderPlacedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(order.getOrderId() != null ? order.getOrderId() : 0L)
                .setOrderNumber(order.getOrderNumber())
                .setCreatedDate(order.getCreatedDate() != null ? order.getCreatedDate().toString() : "")
                .setItems(items)
                .build();
    }

    public OrderItemEvent toOrderItemEvent(OrderLineItems item) {
        return OrderItemEvent.newBuilder()
                .setSkuCode(item.getSkuCode())
                .setQuantity(item.getQuantity())
                .setPrice(item.getPrice() != null ? item.getPrice().toString() : "0")
                .build();
    }
}
