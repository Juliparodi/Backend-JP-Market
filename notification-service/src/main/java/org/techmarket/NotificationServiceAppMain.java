package org.techmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.techmarket.event.OrderPlacedEvent;

@SpringBootApplication
public class NotificationServiceAppMain {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceAppMain.class, args);
    }
}