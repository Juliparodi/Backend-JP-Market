package com.techmarket.orderservice.domain.event;

import lombok.Value;
import java.math.BigDecimal;

@Value
public class OrderItemEvent {
    String skuCode;
    Integer quantity;
    BigDecimal price;
}
