package com.jpmarket.aiopsagent.config;

import com.jpmarket.aiopsagent.application.tools.DeploymentTool;
import com.jpmarket.aiopsagent.application.tools.HealthTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, HealthTool healthTool, DeploymentTool deploymentTool) {
        return builder
                .defaultTools(healthTool, deploymentTool)
                .build();
    }
}
