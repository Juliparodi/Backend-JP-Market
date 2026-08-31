package com.jpmarket.aiopsagent.application.service;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.PrometheusClient;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.PrometheusData;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.PrometheusQueryResponse;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.PrometheusResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private PrometheusClient prometheusClient;

    @InjectMocks
    private MetricsService metricsService;

    private PrometheusQueryResponse buildResponse(String value) {
        PrometheusResult result = new PrometheusResult(Map.of(), List.of(0, value));
        PrometheusData data = new PrometheusData("vector", List.of(result));
        return new PrometheusQueryResponse("success", data);
    }

    // --- getErrorRate ---

    @Test
    void getErrorRate_shouldReturnCorrectPercentage() {
        // 2 errors out of 10 total = 20%
        when(prometheusClient.query(anyString()))
                .thenReturn(buildResponse("2"))   // first call = errorRequests
                .thenReturn(buildResponse("10")); // second call = totalRequests

        double rate = metricsService.getErrorRate("order-service");

        assertThat(rate).isEqualTo(20.0);
    }

    @Test
    void getErrorRate_shouldReturnZeroWhenTotalRequestsIsZero() {
        when(prometheusClient.query(anyString()))
                .thenReturn(buildResponse("0"))
                .thenReturn(buildResponse("0"));

        double rate = metricsService.getErrorRate("order-service");

        assertThat(rate).isEqualTo(0.0);
    }

    @Test
    void getErrorRate_shouldReturnZeroWhenResponseIsNull() {
        when(prometheusClient.query(anyString())).thenReturn(null);

        double rate = metricsService.getErrorRate("order-service");

        assertThat(rate).isEqualTo(0.0);
    }

    @Test
    void getErrorRate_shouldReturnZeroWhenResultListIsEmpty() {
        PrometheusQueryResponse emptyResponse = new PrometheusQueryResponse(
                "success", new PrometheusData("vector", List.of()));

        when(prometheusClient.query(anyString())).thenReturn(emptyResponse);

        double rate = metricsService.getErrorRate("order-service");

        assertThat(rate).isEqualTo(0.0);
    }

    // --- getAverageLatency ---

    @Test
    void getAverageLatency_shouldReturnScalarValue() {
        when(prometheusClient.query(anyString())).thenReturn(buildResponse("145.5"));

        double latency = metricsService.getAverageLatency("order-service");

        assertThat(latency).isEqualTo(145.5);
    }

    @Test
    void getAverageLatency_shouldReturnZeroWhenResponseIsNull() {
        when(prometheusClient.query(anyString())).thenReturn(null);

        double latency = metricsService.getAverageLatency("order-service");

        assertThat(latency).isEqualTo(0.0);
    }

    @Test
    void getAverageLatency_shouldReturnZeroWhenDataIsNull() {
        when(prometheusClient.query(anyString()))
                .thenReturn(new PrometheusQueryResponse("success", null));

        double latency = metricsService.getAverageLatency("order-service");

        assertThat(latency).isEqualTo(0.0);
    }
}
