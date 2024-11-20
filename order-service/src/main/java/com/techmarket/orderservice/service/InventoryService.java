package com.techmarket.orderservice.service;

import java.util.List;

public interface IInventoryService {

    void proccesAndValidateStock(List<String> skuCodes);
}
