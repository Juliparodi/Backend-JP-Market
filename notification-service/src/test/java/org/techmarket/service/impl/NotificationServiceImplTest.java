package org.techmarket.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techmarket.event.OrderPlacedEvent;
import org.techmarket.service.IEmailService;

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
    OrderPlacedEvent event = new OrderPlacedEvent();
    event.setOrderNumber("12345");

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
