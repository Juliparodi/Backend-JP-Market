package com.techmarket.orderservice.service.impl;

import com.techmarket.orderservice.domain.dto.InventoryResponse;
import com.techmarket.orderservice.exceptions.NoInventoriesException;
import com.techmarket.orderservice.exceptions.NoStockException;
import com.techmarket.orderservice.repository.ClientRepository;
import com.techmarket.orderservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ClientRepository clientRepository;
    private final Tracer tracer;

    @Override
    public void processAndValidateStock(List<String> skuCodes) {
        Span span = tracer.nextSpan().name("InventoryServiceLookup").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
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
        } finally {
            span.end();
        }
    }

    private List<InventoryResponse> getInventoryResponse(List<String> skuCodes) {
        return clientRepository.getInventoryResponse(skuCodes);
    }
}
