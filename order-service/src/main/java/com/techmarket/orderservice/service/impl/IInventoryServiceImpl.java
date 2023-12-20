package com.techmarket.orderservice.service.impl;

import com.techmarket.orderservice.domain.dto.InventoryResponse;
import com.techmarket.orderservice.exceptions.NoInventoriesException;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.techmarket.orderservice.constants.Constants.SKU_CODE;

@Service
@RequiredArgsConstructor
public class IInventoryServiceImpl implements IInventoryService {

    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    @Value("${inventory.url}")
    private String INVENTORY_URL;

    @Override
    public void proccesAndValidateStock(List<String> skuCodes) {
        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup").start();
        try {
            List<InventoryResponse> inventoryResponsesList = getInventoryResponse(skuCodes);

            if (inventoryResponsesList.isEmpty()) {
                throw new NoInventoriesException();
            }

            boolean isInStock = inventoryResponsesList.stream()
                    .allMatch(InventoryResponse::getIsInStock);

            if (!isInStock) {
                throw new NoStockException();
            }

        } catch (WebClientException ex) {
            throw new WebClientResponseException(400, "Bad gateway", null, null, null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            inventoryServiceLookup.end();
        }
    }

    private List<InventoryResponse> getInventoryResponse(List<String> skuCodes) {


        return webClientBuilder
                .build().get()
                .uri(INVENTORY_URL, uriBuilder -> uriBuilder.queryParam(SKU_CODE, skuCodes).build())
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();
    }
}
