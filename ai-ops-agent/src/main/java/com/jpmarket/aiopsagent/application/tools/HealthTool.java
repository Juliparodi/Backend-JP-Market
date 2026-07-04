package com.jpmarket.aiopsagent.application.tools;

import com.jpmarket.aiopsagent.infraestructure.adapter.outbound.ServiceHealthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthTool {

    private final ServiceHealthClient serviceHealthClient;

    @Tool(description = "Returns the health status of a microservice")
    public String health(String serviceName) {

        return serviceName + " is " + serviceHealthClient.getHealth(serviceName).status();
    }
}
