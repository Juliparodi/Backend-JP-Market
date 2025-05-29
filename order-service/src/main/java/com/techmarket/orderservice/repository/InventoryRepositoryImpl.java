package com.techmarket.orderservice.repository;

import com.techmarket.orderservice.domain.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.techmarket.orderservice.constants.Constants.SKU_CODE;

@Service
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements ClientRepository {

  private final WebClient.Builder webClientBuilder;

  @Value("${inventory.url}")
  private String INVENTORY_URL;

  @Override
  public List<InventoryResponse> getInventoryResponse(List<String> skuCodes) {
    return webClientBuilder
        .build().get()
        .uri(INVENTORY_URL, uriBuilder -> uriBuilder.queryParam(SKU_CODE, skuCodes).build())
        .retrieve()
        .bodyToFlux(InventoryResponse.class)
        .collectList()
        .block();
  }
}
