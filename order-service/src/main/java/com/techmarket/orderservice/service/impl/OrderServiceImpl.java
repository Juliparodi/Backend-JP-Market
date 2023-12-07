package com.techmarket.orderservice.service.impl;

import com.techmarket.orderservice.domain.dto.InventoryResponse;
import com.techmarket.orderservice.domain.dto.OrderLineItemsDTO;
import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.entities.OrderLineItems;
import com.techmarket.orderservice.exceptions.NoInventoriesException;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.repository.OrderRepository;
import com.techmarket.orderservice.service.IOrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import static com.techmarket.orderservice.constants.Constants.SKU_CODE;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${inventory.url}")
    private String INVENTORY_URL;

    public void placeOrder(OrderRequestDTO orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        if (!productIsInStock(skuCodes)) {
            throw new NoStockException();
        }

        orderRepository.save(order);
    }

    private boolean productIsInStock(List<String> skuCodes) {
        List<InventoryResponse> inventoryResponsesList;
        try {
            inventoryResponsesList = getInventoryResponse(skuCodes);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error occurred", ex);
        }

        if (inventoryResponsesList == null || inventoryResponsesList.isEmpty()) {
           throw new NoInventoriesException();
        }

        return inventoryResponsesList.stream()
                .allMatch(InventoryResponse::getIsInStock);
    }

    private List<InventoryResponse> getInventoryResponse(List<String> skuCodes) {
        return webClientBuilder.build().get()
                .uri(INVENTORY_URL,
                        uriBuilder -> uriBuilder.queryParam(SKU_CODE, skuCodes).build())
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();
    }

    private OrderLineItems mapToDto(OrderLineItemsDTO orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}