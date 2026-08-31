package com.jpmarket.aiopsagent.application.tools;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.ServiceHealthClient;
import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.dto.HealthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthToolTest {

    @Mock
    private ServiceHealthClient serviceHealthClient;

    @InjectMocks
    private HealthTool healthTool;

    @Test
    void health_shouldReturnUpStatus() {
        when(serviceHealthClient.getHealth("order-service")).thenReturn(new HealthResponse("UP"));

        String result = healthTool.health("order-service");

        assertThat(result).isEqualTo("order-service is UP");
    }

    @Test
    void health_shouldReturnDownStatus() {
        when(serviceHealthClient.getHealth("inventory-service")).thenReturn(new HealthResponse("DOWN"));

        String result = healthTool.health("inventory-service");

        assertThat(result).isEqualTo("inventory-service is DOWN");
    }
}
