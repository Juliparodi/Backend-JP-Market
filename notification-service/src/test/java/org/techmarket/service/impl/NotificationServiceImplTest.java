package org.techmarket.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techmarket.event.OrderItemEvent;
import com.techmarket.schema.OrderPlacedEvent;
import org.techmarket.service.IEmailService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

  @Mock
  private IEmailService emailService;

  private NotificationServiceImpl notificationService;

  @BeforeEach
  void setUp() {
    notificationService = new NotificationServiceImpl(emailService);
  }


  @Test
  void whenHandleNotification_thenShouldSendEmail() {

    // Given
    OrderPlacedEvent event = OrderPlacedEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setOrderId(1L)
            .setOrderNumber("12345")
            .setCreatedDate(LocalDateTime.now().toString())
            .setItems(java.util.Collections.emptyList())
            .build();

    // When
    notificationService.handleNotification(event);

    // Then
    verify(emailService).sendMessage(
            eq("julianparodi19@gmail.com"),
            eq("Order #12345 Confirmation"),
            eq("Congrats! Order nro: 12345 succesfully created")
    );
  }
}
