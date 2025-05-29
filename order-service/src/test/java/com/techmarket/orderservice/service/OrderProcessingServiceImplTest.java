package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.OrderLineItemsDTO;
import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.event.OrderPlacedEvent;
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
import java.util.List;

import static com.techmarket.orderservice.mocks.OrderMocks.getOrderMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OrderProcessingServiceImplTest {

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

  @Test
  void whenInventoryTakesMoreThan4SecsInRespond_ThenThrowTimeout() {
    when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.build()).thenReturn(WebClient.builder().baseUrl(INVENTORY_URL).build());
    when(orderService.createOrder(any())).thenReturn(getOrderMock());
    when(orderService.extractSkuCodes(any())).thenReturn(getSkuCodes());
    doAnswer(invocation -> {
      Thread.sleep(5000);
      return null;
    }).when(inventoryService).processAndValidateStock(any());

    OrderRequestDTO orderRequest = new OrderRequestDTO();
    orderRequest.setOrderLineItemsDtoList(List.of(OrderLineItemsDTO.builder().skuCode("iphone_13").build()));

    Assertions.assertThrows(RuntimeException.class, () ->
        orderProccessingService.placeOrder(orderRequest));

  }

    private List<String> getSkuCodes() {
      return List.of("skuCode123");
    }

}