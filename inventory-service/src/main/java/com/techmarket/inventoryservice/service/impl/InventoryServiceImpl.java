package com.techmarket.inventoryservice.service.impl;

import com.techmarket.inventoryservice.domain.dto.InventoryResponse;
import com.techmarket.inventoryservice.domain.entities.Inventory;
import com.techmarket.inventoryservice.domain.event.OrderItemEvent;
import com.techmarket.inventoryservice.domain.event.OrderPlacedEvent;
import com.techmarket.inventoryservice.repository.InventoryRepository;
import com.techmarket.inventoryservice.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class InventoryServiceImpl implements IInventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);
        if (!CollectionUtils.isEmpty(inventories)) {
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

    @KafkaListener(topics = "orderTopic")
    @Transactional
    public void updateInventory(OrderPlacedEvent orderPlacedEvent) {

        Map<String, Integer> orderedQuantities =
                orderPlacedEvent.getItems()
                        .stream()
                        .collect(Collectors.toMap(
                                OrderItemEvent::getSkuCode,
                                OrderItemEvent::getQuantity
                        ));

        List<Inventory> updatedInventories =
                inventoryRepository.findBySkuCodeIn(orderedQuantities.keySet().stream().toList())
                        .stream()
                        .map(inventory -> {
                            int newQty = inventory.getQuantity()
                                    - orderedQuantities.getOrDefault(inventory.getSkuCode(), 0);

                            if (newQty < 0) {
                                throw new RuntimeException(
                                        "Not enough stock for SKU: " + inventory.getSkuCode()
                                );
                            }

                            inventory.setQuantity(newQty);
                            return inventory;
                        })
                        .toList();

        inventoryRepository.saveAll(updatedInventories);

        log.info("Inventory updated for order {}", orderPlacedEvent.getOrderNumber());
    }

}
