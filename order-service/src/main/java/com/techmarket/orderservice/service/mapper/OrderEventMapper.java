package com.techmarket.orderservice.service.mapper;

import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.entities.OrderLineItems;
import com.techmarket.orderservice.domain.event.OrderItemEvent;
import com.techmarket.orderservice.domain.event.OrderPlacedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    @Mapping(source = "orderLineItemsList", target = "items")
    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    OrderPlacedEvent toOrderPlacedEvent(Order order);

    OrderItemEvent toOrderItemEvent(OrderLineItems item);
}
