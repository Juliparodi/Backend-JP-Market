package com.jpmarket.aiopsagent.infraestructure.adapter.inbound;

import com.jpmarket.aiopsagent.application.service.MetricsService;
import com.jpmarket.aiopsagent.infraestructure.adapter.inbound.record.ChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private ChatClient chatClient;

    @MockitoBean
    private MetricsService metricsService;

    @Test
    void chat_shouldReturnAiResponse() throws Exception {
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("order-service is UP");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new ChatRequest("Is order-service healthy?"))))
                .andExpect(status().isOk())
                .andExpect(content().string("order-service is UP"));
    }

    @Test
    void errorRate_shouldReturnMetricValue() throws Exception {
        when(metricsService.getErrorRate("order-service")).thenReturn(15.5);

        mockMvc.perform(get("/api/chat/metrics/error-rate/order-service"))
                .andExpect(status().isOk())
                .andExpect(content().string("15.5"));
    }

    @Test
    void errorRate_shouldReturnZeroWhenNoErrors() throws Exception {
        when(metricsService.getErrorRate("product-service")).thenReturn(0.0);

        mockMvc.perform(get("/api/chat/metrics/error-rate/product-service"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }
}