package com.techmarket.orderservice.repository;

import com.techmarket.orderservice.domain.dto.InventoryResponse;

import java.util.List;

public interface ClientRepository {

  List<InventoryResponse> getInventoryResponse(List<String> skuCodes);
}
