package com.techmarket.orderservice.controller;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.service.IOrderProccessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

  @Mock
  private IOrderProccessingService orderProcessingService;

  @InjectMocks
  private OrderController orderController;

  private OrderRequestDTO mockRequest;

  @BeforeEach
  void setUp() {
    mockRequest = new OrderRequestDTO();
  }

  @Test
  void whenRequestingPlaceOrder_thenShouldReturnCompletableFutureOfString() throws Exception {
    // Arrange
    String expectedResponse = "Order placed successfully";
    when(orderProcessingService.placeOrder(any(OrderRequestDTO.class)))
        .thenReturn(expectedResponse);

    // Act
    CompletableFuture<String> responseFuture = orderController.placeOrder(mockRequest);

    // Assert
    assertEquals(expectedResponse, responseFuture.get());
    verify(orderProcessingService, times(1)).placeOrder(mockRequest);
  }

  @Test
  void whenRequestingPlaceOrderFails_thenShouldReturn500() throws Exception {
    // Arrange
    WebClientResponseException exception = WebClientResponseException.create(
        500, "Internal Server Error", null, null, null);

    // Act
    CompletableFuture<ResponseEntity<String>> responseFuture =
        orderController.fallbackMethod(mockRequest, exception);

    // Assert
    ResponseEntity<String> response = responseFuture.get();
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertEquals("Service is temporarily unavailable. Circuit breaker open.", response.getBody());
  }
}
