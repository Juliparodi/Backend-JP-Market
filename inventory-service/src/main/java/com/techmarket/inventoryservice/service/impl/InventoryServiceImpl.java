package com.techmarket.inventoryservice.service.impl;

import com.techmarket.inventoryservice.domain.dto.InventoryResponse;
import com.techmarket.inventoryservice.domain.entities.Inventory;
import com.techmarket.inventoryservice.repository.InventoryRepository;
import com.techmarket.inventoryservice.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements IInventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);
        if (inventories != null && !inventories.isEmpty()) {
            return inventories.stream()
                    .map(inventory ->
                            InventoryResponse.builder()
                                    .skuCode(inventory.getSkuCode())
                                    .isInStock(inventory.getQuantity() > 0)
                                    .build()
                    )
                    .toList();
        }
        return null;
    }

}
