package org.techmarket.service;

import org.techmarket.event.OrderPlacedEvent;

public interface INotificationService {

    void handleNotification(OrderPlacedEvent orderPlacedEvent);
}
