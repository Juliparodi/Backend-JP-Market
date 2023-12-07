package com.techmarket.orderservice.integration;

import com.techmarket.orderservice.domain.dto.OrderLineItemsDTO;
import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.exceptions.NoInventoriesException;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.repository.OrderRepository;
import com.techmarket.orderservice.service.impl.OrderServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OrderServiceTest {

    private MockWebServer mockWebServer;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private OrderServiceImpl orderService;

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

        orderService.placeOrder(orderRequest);

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void whenOrderHasProductsWithNoStock_thenThrowNoStockException() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(WebClient.builder().baseUrl(INVENTORY_URL).build());

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setOrderLineItemsDtoList(Collections.emptyList());

        mockWebServer.enqueue(new MockResponse()
                .setBody("[\n" +
                        "    {\n" +
                        "        \"skuCode\": \"iphone_13\",\n" +
                        "        \"isInStock\": false\n" +
                        "    }\n" +
                        "]")
                .addHeader("Content-Type", "application/json"));

        assertThrows(NoStockException.class, () -> orderService.placeOrder(orderRequest));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void whenOrderHasNoProductInInventory_thenThrowNoStockException() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(WebClient.builder().baseUrl(INVENTORY_URL).build());

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setOrderLineItemsDtoList(Collections.emptyList());

        mockWebServer.enqueue(new MockResponse()
                .setBody("[]")
                .addHeader("Content-Type", "application/json"));

        assertThrows(NoInventoriesException.class, () -> orderService.placeOrder(orderRequest));

        verify(orderRepository, never()).save(any());
    }
}
