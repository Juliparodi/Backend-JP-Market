package com.jpmarket.aiopsagent.infraestructure.adapter.outbound;

import com.jpmarket.aiopsagent.config.ServicesProperties;
import com.jpmarket.aiopsagent.domain.enums.ServiceName;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class ServiceHealthClient {

    private final RestClient.Builder restClient;
    private final ServicesProperties servicesProperties;

    public HealthResponse getHealth(String serviceName) {
        ServiceName service = ServiceName.from(serviceName);

        String baseUrl = servicesProperties
                .getServices()
                .get(service.getPropertyName())
                .getBaseUrl();

        return restClient.build()
                .get()
                .uri(baseUrl + "/actuator/health")
                .retrieve()
                .body(HealthResponse.class);
    }
}
