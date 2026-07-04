package com.jpmarket.aiopsagent.infraestructure.adapter.inbound;

import com.jpmarket.aiopsagent.infraestructure.adapter.inbound.record.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        return chatClient.prompt(request.message())
                .call()
                .content();
    }
}
