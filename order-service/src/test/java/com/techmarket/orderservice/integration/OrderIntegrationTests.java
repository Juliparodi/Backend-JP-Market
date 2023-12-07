package com.techmarket.orderservice.integration;

import com.techmarket.orderservice.repository.OrderRepository;
import com.techmarket.orderservice.utils.JsonConverter;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

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
        Assertions.assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    void whenSavingOrderWithProductsWithNoStock_thenReturnBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/techMarket/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                        .content(JsonConverter.loadJsonFromFile("order-no-stock.json")))
                .andExpect(status().isBadRequest());
    }

}
