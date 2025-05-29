package com.techmarket.inventoryservice.service;

import com.techmarket.inventoryservice.domain.dto.InventoryResponse;

import java.util.List;

public interface IInventoryService {

    List<InventoryResponse> isInStock(List<String> skuCodes);

}
