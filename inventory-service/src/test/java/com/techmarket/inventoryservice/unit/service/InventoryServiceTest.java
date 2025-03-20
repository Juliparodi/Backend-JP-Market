package com.techmarket.inventoryservice.unit.service;

import com.techmarket.inventoryservice.domain.dto.InventoryResponse;
import com.techmarket.inventoryservice.domain.entities.Inventory;
import com.techmarket.inventoryservice.repository.InventoryRepository;
import com.techmarket.inventoryservice.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {


  @Mock
  InventoryRepository inventoryRepository;

  @InjectMocks
  private InventoryServiceImpl inventoryService;

  @Test
  void testProcessAndValidateStock_StockAvailable() {
    List<String> skuCodes = List.of("123, 1234");
    when(inventoryRepository.findBySkuCodeIn(skuCodes))
        .thenReturn(List.of(
            Inventory.builder()
                .id(1L)
                .skuCode("123")
                .quantity(23)
                .build(),
            Inventory.builder()
                .id(0L)
                .skuCode("1234")
                .quantity(0)
                .build()
        ));

    // Call the method under test
    List<InventoryResponse> inventoryResponses = inventoryService.isInStock(skuCodes);

    // Verify repository was called
    verify(inventoryRepository, times(1)).findBySkuCodeIn(skuCodes);
    assertEquals(2, inventoryResponses.size());
    assertEquals("123", inventoryResponses.get(0).getSkuCode());
    assertEquals(true, inventoryResponses.get(0).getIsInStock());
    assertEquals("1234", inventoryResponses.get(1).getSkuCode());
    assertEquals(false, inventoryResponses.get(1).getIsInStock());

  }

  @Test
  void whenNoSkuCodeId_ThenReturnNull() {
    List<String> skuCodes = List.of("123, 1234");
    when(inventoryRepository.findBySkuCodeIn(skuCodes))
        .thenReturn(null);

    // Call the method under test
    List<InventoryResponse> inventoryResponses = inventoryService.isInStock(skuCodes);

    assertNull(inventoryResponses);

  }

  @Test
  void whenNoSkuCodeIdEmptyList_ThenReturnNull() {
    List<String> skuCodes = List.of("123, 1234");
    when(inventoryRepository.findBySkuCodeIn(skuCodes))
        .thenReturn(Collections.emptyList());

    // Call the method under test
    List<InventoryResponse> inventoryResponses = inventoryService.isInStock(skuCodes);

    assertNull(inventoryResponses);

  }
}
