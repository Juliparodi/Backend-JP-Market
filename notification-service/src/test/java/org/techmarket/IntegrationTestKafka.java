package org.techmarket;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = "orderTopic")
@ActiveProfiles("test")
public class IntegrationTestKafka {

    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer();

    private static Producer<String, String> kafkaProducer;
    private static Consumer<String, String> kafkaConsumer;


    @BeforeAll
    static void setUp() {
        kafkaContainer.start();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Properties props2 = new Properties();
        props2.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props2.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props2.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props2.put(ConsumerConfig.GROUP_ID_CONFIG, "notificationId");

        kafkaProducer = new KafkaProducer<>(props);
        kafkaConsumer = new KafkaConsumer<>(props2);

        kafkaConsumer.subscribe(Collections.singleton("orderTopic"));

    }

    @AfterAll
    static void tearDown() {
        kafkaProducer.close();
        kafkaContainer.stop();
    }

    @Test
    public void testKafkaConsumer() {
        // Simulate producing a message to orderTopic
        String message = "{\"orderNumber\":\"12345\"}"; // Replace this with your JSON message
        kafkaProducer.send(new ProducerRecord<>("orderTopic", message));

        // Add a short delay to ensure the message is processed by Kafka
        try {
            Thread.sleep(1000); // Adjust as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now the Kafka consumer part to consume the produced message
        // Wait for records from Kafka
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));

        // Process received records or assert on them
        records.forEach(record -> {
            // Your assertions here
            Assertions.assertEquals("12345", record.value());
        });
    }
}
