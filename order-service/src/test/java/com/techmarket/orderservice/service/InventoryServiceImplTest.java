package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.InventoryResponse;
import com.techmarket.orderservice.exceptions.NoInventoriesException;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.repository.ClientRepository;
import com.techmarket.orderservice.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {


  @Mock
  private ClientRepository clientRepository;

  @InjectMocks
  private InventoryServiceImpl inventoryService;


  @Test
  void testProcessAndValidateStock_StockAvailable() {
    // Mock repository response: all items are in stock
    when(clientRepository.getInventoryResponse(anyList()))
        .thenReturn(List.of(
            new InventoryResponse("sku1", true),
            new InventoryResponse("sku2", true)
        ));

    // Call the method under test
    inventoryService.processAndValidateStock(List.of("sku1", "sku2"));

    // Verify repository was called
    verify(clientRepository, times(1)).getInventoryResponse(anyList());

  }

  @Test
  void testProcessAndValidateStock_StockUnavailable() {
    // Mock repository response: one item is out of stock
    when(clientRepository.getInventoryResponse(anyList()))
        .thenReturn(List.of(
            new InventoryResponse("sku1", true),
            new InventoryResponse("sku2", false)
        ));

    // Assert that NoStockException is thrown
    assertThrows(NoStockException.class, () ->
        inventoryService.processAndValidateStock(List.of("sku1", "sku2"))
    );

  }

  @Test
  void testProcessAndValidateStock_NoInventoryResponse() {
    // Mock repository response: empty response
    when(clientRepository.getInventoryResponse(anyList()))
        .thenReturn(List.of());

    // Assert that NoInventoriesException is thrown
    assertThrows(NoInventoriesException.class, () ->
        inventoryService.processAndValidateStock(List.of("sku1", "sku2"))
    );

  }

  @Test
  void testProcessAndValidateStock_WebClientException() {
    // Mock repository to throw a WebClientException
    doThrow(WebClientResponseException.class).when(clientRepository).getInventoryResponse(anyList());

    // Assert that WebClientResponseException is thrown
    assertThrows(WebClientResponseException.class, () ->
        inventoryService.processAndValidateStock(List.of("sku1", "sku2"))
    );

  }

  @Test
  void testProcessAndValidateStock_GenericException() {
    // Mock repository to throw a generic exception
    when(clientRepository.getInventoryResponse(anyList()))
        .thenThrow(new RuntimeException("Unexpected error"));

    // Assert that RuntimeException is thrown
    assertThrows(RuntimeException.class, () ->
        inventoryService.processAndValidateStock(List.of("sku1", "sku2"))
    );

  }


}
