package com.techmarket.orderservice.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties properties) {
        Map<String, Object> configs = properties.buildProducerProperties();

        // 2. Instruct the serializer to search using the Application's ClassLoader
        configs.put("producer.classloader", Thread.currentThread().getContextClassLoader());


        KafkaAvroSerializer valueSerializer = new KafkaAvroSerializer();
        // Crucial: Passing 'false' prevents background/lazy thread-bound configuration loading
        valueSerializer.configure(configs, false);

        return new DefaultKafkaProducerFactory<>(
                configs,
                new StringSerializer(),
                valueSerializer
        );
    }
}
