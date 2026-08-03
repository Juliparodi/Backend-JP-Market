package com.jpmarket.aiopsagent.infraestructure.adapter.inbound;

import com.jpmarket.aiopsagent.application.service.MetricsService;
import com.jpmarket.aiopsagent.infraestructure.adapter.inbound.record.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final MetricsService metricsService;

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        return chatClient.prompt(request.message())
                .call()
                .content();
    }


    @GetMapping("/metrics/error-rate/{service}")
    public double errorRate(@PathVariable String service) {
        return metricsService.getErrorRate(service);
    }
}
