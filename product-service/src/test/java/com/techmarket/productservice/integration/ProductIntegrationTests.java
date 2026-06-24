package com.techmarket.productservice.integration;

import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.repository.PromotionRepository;
import com.techmarket.productservice.utils.JsonConverter;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class ProductIntegrationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
        .withEnv("MONGO_INITDB_DATABASE", "local")
        .withEnv("MONGO_INIT_ROOT_USERNAME", "admin")
        .withEnv("MONGO_INIT_ROOT_PASSWORD", "admin");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${token}")
    private String TOKEN;

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl());
    }

    @AfterEach
    void cleanUp() {
        this.productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConverter.loadJsonFromFile("new-product.json"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(status().isCreated());
        assertEquals(18, productRepository.findAll().size());
    }

    @Test
    void shouldGetProducts() throws Exception {
        insertValuesDB();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category.name").value("phone"));;
    }

    private void insertValuesDB() {
        Promotion promotion = Promotion.builder()
                .promotionCode("PHO300")
                .name("Phone discounts")
                .description("Phone discounts")
                .discountRate(BigDecimal.valueOf(0.15))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();

        promotion = promotionRepository.save(promotion);

        categoryRepository.save(
                Category.builder().id((new ObjectId())).name("Phone").promotion(promotion).build()
        );

        Category phoneCategory = categoryRepository.findByName("Phone").get();

        productRepository.save(Product.builder().id(new ObjectId()).name("Phone").nameWithDetail("Phone detail").category(phoneCategory.getId()).variations(Collections.emptyList()).build());
    }

}
