package com.techmarket.orderservice.mocks;

import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.entities.OrderLineItems;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderMocks {

  public static Order getOrderMock() {
    return Order.builder()
        .orderId(123L)
        .orderNumber("1234O")
        .createdDate(LocalDate.of(2024, 8, 11).atStartOfDay())
        .orderLineItemsList(List.of(
            OrderLineItems.builder()
                .price(new BigDecimal(123))
                .quantity(12)
                .skuCode("skuCode1")
                .build())
        )
        .build();
  }
}
