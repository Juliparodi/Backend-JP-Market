package com.techmarket.orderservice.integration;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.entities.OrderLineItems;
import com.techmarket.orderservice.domain.event.OrderPlacedEvent;
import com.techmarket.orderservice.service.IOrderService;
import com.techmarket.orderservice.service.InventoryService;
import com.techmarket.orderservice.service.impl.OrderProcessingServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
public class OrderProcessingServiceKafkaIT {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"))
            .withReuse(false);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OrderProcessingServiceImpl service;

    @MockitoBean
    private IOrderService orderService;

    @MockitoBean
    private InventoryService inventoryService;

    private KafkaConsumer<String, OrderPlacedEvent> consumer;

    @BeforeEach
    void setupConsumer() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "techmarket-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(OrderPlacedEvent.class, false)
        );

        consumer.subscribe(List.of("orderTopic"));
    }

    @Autowired
    Environment env;


    @Test
    void shouldSendKafkaEventWhenOrderIsPlaced() {

        // given
        OrderRequestDTO request = new OrderRequestDTO();

        Order order = new Order();
        order.setOrderNumber("ORDER-123");
        order.setOrderLineItemsList(List.of(
                OrderLineItems.builder().orderLineItemsId(123L).skuCode("SKU1").price(BigDecimal.valueOf(100.0)).quantity(2).order(order).build(),
                OrderLineItems.builder().orderLineItemsId(123L).skuCode("SKU2").price(BigDecimal.valueOf(200.0)).quantity(2).order(order).build()));

        when(orderService.createOrder(request)).thenReturn(order);
        when(orderService.extractSkuCodes(order)).thenReturn(List.of("SKU1", "SKU2"));

        doNothing().when(inventoryService).processAndValidateStock(anyList());
        doNothing().when(orderService).saveOrder(order);

        System.out.println("PROPERTY" +
                env.getProperty("spring.kafka.bootstrap-servers")
        );
        // when
        String result = service.placeOrder(request);

        // then
        assertEquals("Order placed successfully", result);

        // verify Kafka message
        ConsumerRecords<String, OrderPlacedEvent> records =
                consumer.poll(Duration.ofSeconds(5));

        assertThat(records.count()).isGreaterThan(0);

        ConsumerRecord<String, OrderPlacedEvent> record =
                records.iterator().next();

        assertEquals("ORDER-123", record.value().orderNumber());
        assertEquals("SKU1", record.value().items().get(0).skuCode());

    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

}
