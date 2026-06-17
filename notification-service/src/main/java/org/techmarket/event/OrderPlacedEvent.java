package org.techmarket.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(UUID eventId,
                               Long orderId,
                               String orderNumber,
                               LocalDateTime createdDate,
                               List<OrderItemEvent> items) {

}
