package com.jpmarket.aiopsagent.infraestructure.adapter.outbound;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.PrometheusQueryResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Log4j2
public class PrometheusClient {

    private final RestClient restClient;

    public PrometheusClient(
            RestClient.Builder builder,
            @Value("${jpmarket.prometheus.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public PrometheusQueryResponse query(String promQl) {

        log.info("Executing PromQL: {}", promQl);

        String url = "/api/v1/query?query=" +
                URLEncoder.encode(promQl, StandardCharsets.UTF_8);

        return restClient.get()
                .uri(URI.create(url))
                .retrieve()
                .body(PrometheusQueryResponse.class);
    }

}