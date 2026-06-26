package com.techmarket.orderservice.domain.event;

import lombok.Value;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class OrderPlacedEvent {
    UUID eventId;
    Long orderId;
    String orderNumber;
    LocalDateTime createdDate;
    List<OrderItemEvent> items;

}
