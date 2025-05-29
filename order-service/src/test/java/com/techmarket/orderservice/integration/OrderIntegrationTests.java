package com.techmarket.orderservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.techmarket.orderservice.repository.OrderRepository;
import com.techmarket.orderservice.utils.JsonConverter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderIntegrationTests {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.26"))
            .withDatabaseName("defaultdb")
            .withUsername("avnadmin")
            .withPassword("AVNS_ViMLlH4Ah3IR23tl_T1")
            .withInitScript("sql/init.sql");

    private WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Value("${token}")
    private String TOKEN;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void whenSavingOrderWithProductsWithStock_thenReturnCreated() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.post("/api/techMarket/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                        .content(JsonConverter.loadJsonFromFile("new-order.json")))
                .andExpect(status().isCreated());
    }

    /*
    @Test
    void whenSavingOrderWithProductsWithNoStock_thenReturnBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/techMarket/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                        .content(JsonConverter.loadJsonFromFile("order-no-stock.json")))
                .andExpect(status().isBadRequest());
    }

     */


}
