package org.techmarket.integration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class IntegrationTestKafka {

    @Container
    private static final KafkaContainer kafkaContainer =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

    private static Producer<String, String> kafkaProducer;
    private static Consumer<String, String> kafkaConsumer;

    @DynamicPropertySource
    static void configureKafkaBootstrap(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "notificationId");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Important for tests

        kafkaProducer = new KafkaProducer<>(producerProps);
        kafkaConsumer = new KafkaConsumer<>(consumerProps);

        kafkaConsumer.subscribe(Collections.singleton("orderTopic"));

    }

    @AfterAll
    static void tearDown() {
        kafkaProducer.close();
        kafkaConsumer.close();
        kafkaContainer.stop();
    }

    @Test
    public void testKafkaConsumer() {
        String message = "{\"orderNumber\":\"12345\"}";
        kafkaProducer.send(new ProducerRecord<>("orderTopic", message));

        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));

        Assertions.assertFalse(records.isEmpty(), "No records were consumed from Kafka");

        records.forEach(record -> {
            Assertions.assertEquals("{\"orderNumber\":\"12345\"}", record.value());
        });
    }
}
