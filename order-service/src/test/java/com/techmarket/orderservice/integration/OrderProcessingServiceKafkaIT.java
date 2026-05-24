package com.techmarket.orderservice.integration;

import com.techmarket.orderservice.domain.dto.OrderRequestDTO;
import com.techmarket.orderservice.domain.entities.Order;
import com.techmarket.orderservice.domain.event.OrderPlacedEvent;
import com.techmarket.orderservice.service.IOrderService;
import com.techmarket.orderservice.service.InventoryService;
import com.techmarket.orderservice.service.impl.OrderProcessingServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        topics = "orderTopic",
        partitions = 1
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
public class OrderProcessingServiceKafkaIT {

    @Autowired
    private OrderProcessingServiceImpl service;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private IOrderService orderService;

    @MockitoBean
    private InventoryService inventoryService;

    private org.apache.kafka.clients.consumer.Consumer<String, OrderPlacedEvent> consumer;


    @BeforeEach
    void setUp() {

        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps(
                        "test-group",
                        "false",
                        embeddedKafkaBroker
                );

        JacksonJsonDeserializer<OrderPlacedEvent> deserializer =
                new JacksonJsonDeserializer<>(OrderPlacedEvent.class);

        deserializer.addTrustedPackages("*");

        consumer =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        deserializer
                ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(
                consumer,
                "orderTopic"
        );
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void shouldSendKafkaEventWhenOrderIsPlaced() {

        // given
        OrderRequestDTO request = new OrderRequestDTO();

        Order order = new Order();
        order.setOrderNumber("ORDER-123");

        when(orderService.createOrder(request))
                .thenReturn(order);

        when(orderService.extractSkuCodes(order))
                .thenReturn(List.of("SKU1", "SKU2"));

        doNothing()
                .when(inventoryService)
                .processAndValidateStock(anyList());

        doNothing()
                .when(orderService)
                .saveOrder(order);

        // when
        String result = service.placeOrder(request);

        // then
        assertThat(result)
                .isEqualTo("Order placed successfully");

        ConsumerRecord<String, OrderPlacedEvent> record =
                KafkaTestUtils.getSingleRecord(
                        consumer,
                        "orderTopic",
                        Duration.ofSeconds(10)
                );

        assertThat(record).isNotNull();

        assertThat(record.key())
                .isEqualTo("ORDER-123");

        assertThat(record.value())
                .isNotNull();

        assertThat(record.value().orderNumber())
                .isEqualTo("ORDER-123");
    }

}
