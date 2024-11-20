package com.techmarket.orderservice.unit.service;

import com.techmarket.orderservice.domain.dto.OrderLineItemsDTO;
import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.entities.OrderLineItems;
import com.techmarket.orderservice.event.OrderPlacedEvent;
import com.techmarket.orderservice.exceptions.NoInventoriesException;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.repository.OrderRepository;
import com.techmarket.orderservice.service.IOrderService;
import com.techmarket.orderservice.service.InventoryService;
import com.techmarket.orderservice.service.impl.OrderProcessingServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OrderServiceTest {

  private MockWebServer mockWebServer;

  @Mock
  private IOrderService orderService;

  @Mock
  private InventoryService inventoryService;

  @Mock
  private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

  @Mock
  private WebClient.Builder webClientBuilder;

  @InjectMocks
  private OrderProcessingServiceImpl orderProccessingService;

  private String INVENTORY_URL;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    INVENTORY_URL = mockWebServer.url("/inventory").toString();
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void whenOrderHasProductsWithStock_thenSaveOrder() {
    when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.build()).thenReturn(WebClient.builder().baseUrl(INVENTORY_URL).build());
    when(orderService.createOrder(any())).thenReturn(getOrderMock());
    when(orderService.extractSkuCodes(any())).thenReturn(getSkuCodes());
    doNothing().when(inventoryService).processAndValidateStock(anyList());

    OrderRequestDTO orderRequest = new OrderRequestDTO();
    orderRequest.setOrderLineItemsDtoList(List.of(OrderLineItemsDTO.builder().skuCode("iphone_13").build()));

    mockWebServer.enqueue(new MockResponse()
        .setBody("[\n" +
            "    {\n" +
            "        \"skuCode\": \"iphone_13\",\n" +
            "        \"isInStock\": true\n" +
            "    }\n" +
            "]")
        .addHeader("Content-Type", "application/json"));

    String response = orderProccessingService.placeOrder(orderRequest);

    Assertions.assertEquals("Order placed successfully", response);
  }

    private List<String> getSkuCodes() {
      return List.of("skuCode123");
    }

  private Order getOrderMock() {
    return Order.builder()
        .orderId(123L)
        .orderNumber("1234O")
        .createdDate(LocalDate.of(2024, 8, 11).atStartOfDay())
        .orderLineItemsList(List.of(
            OrderLineItems.builder()
                .price(new BigDecimal(123))
                .quantity(12)
                .skuCode("skuCode1")
                .build())
        )
        .build();
  }
}