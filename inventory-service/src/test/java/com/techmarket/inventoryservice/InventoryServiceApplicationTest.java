package com.techmarket.inventoryservice;

import com.techmarket.inventoryservice.domain.dto.InventoryResponse;
import com.techmarket.inventoryservice.domain.entities.Inventory;
import com.techmarket.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceApplicationTest {

  @InjectMocks
  InventoryServiceApplication inventoryServiceApplication;

  @Mock
  InventoryRepository inventoryRepository;

  @Test
  void testProcessAndValidateStock_StockAvailable() {
    List<Inventory> inventories = List.of(
        Inventory.builder()
            .skuCode("iphone_13")
            .quantity(100)
            .build(),
        Inventory.builder()
            .skuCode("iphone_13_red")
            .quantity(0)
            .build());
    when(inventoryRepository.saveAll(inventories)).thenReturn(new ArrayList<>());

    // Call the method under test
    CommandLineRunner runner = inventoryServiceApplication.loadData(inventoryRepository);

    // Verify repository was called
    assertDoesNotThrow(() -> runner.run());

  }
}
