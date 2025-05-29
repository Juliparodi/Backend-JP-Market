package com.techmarket.orderservice.service;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.repository.OrderRepository;
import com.techmarket.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.techmarket.orderservice.mocks.OrderMocks.getOrderMock;
import static com.techmarket.orderservice.utils.JsonConverter.loadJsonFromFile;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OrderServiceImplTest {

  @InjectMocks
  private OrderServiceImpl orderService;

  @Mock
  private OrderRepository orderRepository;

  @Test
  void whenCreateOrderFromDto_thenReturnOrder() throws IOException, URISyntaxException {
    OrderRequestDTO orderRequest = loadJsonFromFile("new-order.json", OrderRequestDTO.class);

    Order response = orderService.createOrder(orderRequest);

    Assertions.assertEquals(response.getOrderLineItemsList().get(0).getSkuCode(),"iphone_13");
  }

  @Test
  void whenGetSkuCodes_thenReturnSkuCodes() {
    List<String> response = orderService.extractSkuCodes(getOrderMock());

    Assertions.assertEquals("skuCode1",response.get(0));
  }
}
