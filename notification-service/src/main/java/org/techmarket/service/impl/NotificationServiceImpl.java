package org.techmarket.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.techmarket.event.OrderPlacedEvent;
import org.techmarket.service.IEmailService;
import org.techmarket.service.INotificationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final IEmailService emailService;

    @KafkaListener(topics = "orderTopic")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
        String orderNumber = orderPlacedEvent.getOrderNumber();
        String subject = "Order #" + orderNumber + " Confirmation";
        String congrats = "Congrats! Order nro: " + orderNumber + " succesfully created";
        log.info("ACK: {}", orderNumber);
        emailService.sendMessage("julianparodi19@gmail.com", subject, congrats);
    }
}
