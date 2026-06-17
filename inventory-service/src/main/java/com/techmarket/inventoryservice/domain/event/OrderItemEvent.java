package com.techmarket.inventoryservice.domain.event;

import java.math.BigDecimal;

public record OrderItemEvent(String skuCode,
                             Integer quantity,
                             BigDecimal price) {
}
