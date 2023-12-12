package org.techmarket.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.techmarket.event.OrderPlacedEvent;

public interface INotificationService {

    void handleNotification(OrderPlacedEvent orderPlacedEvent);
}
