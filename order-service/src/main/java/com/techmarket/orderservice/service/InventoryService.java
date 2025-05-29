package com.techmarket.orderservice.service;

import java.util.List;

public interface InventoryService {

    void processAndValidateStock(List<String> skuCodes);
}
