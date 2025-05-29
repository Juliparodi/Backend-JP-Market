package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.InventoryResponse;
import com.techmarket.orderservice.exceptions.NoInventoriesException;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.repository.ClientRepository;
import com.techmarket.orderservice.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class InventoryServiceImplTest {


  @Mock
  private ClientRepository clientRepository;

  @Mock
  private Tracer tracer;

  @Mock
  private Span span;

  @InjectMocks
  private InventoryServiceImpl inventoryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Mock tracer behavior
    when(tracer.nextSpan()).thenReturn(span);
    when(tracer.nextSpan().name(anyString())).thenReturn(span);
    when(tracer.nextSpan().name(anyString()).start()).thenReturn(span);
  }

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

    // Verify span is started and ended
    verify(span, times(1)).start();
    verify(span, times(1)).end();
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

    // Verify span is ended even after an exception
    verify(span, times(1)).end();
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

    // Verify span is ended
    verify(span, times(1)).end();
  }

  @Test
  void testProcessAndValidateStock_WebClientException() {
    // Mock repository to throw a WebClientException
    doThrow(WebClientResponseException.class).when(clientRepository).getInventoryResponse(anyList());

    // Assert that WebClientResponseException is thrown
    assertThrows(WebClientResponseException.class, () ->
        inventoryService.processAndValidateStock(List.of("sku1", "sku2"))
    );

    // Verify span is ended
    verify(span, times(1)).end();
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

    // Verify span is ended
    verify(span, times(1)).end();
  }


}
