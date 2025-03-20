package com.techmarket.inventoryservice.domain.dto;

import lombok.*;

@Value
@AllArgsConstructor
@Builder
public class InventoryResponse {

    String skuCode;
    Boolean isInStock;
}
