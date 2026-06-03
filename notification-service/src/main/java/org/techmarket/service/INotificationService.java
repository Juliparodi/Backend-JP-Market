package org.techmarket.service;

import com.techmarket.schema.OrderPlacedEvent;

public interface INotificationService {

    void handleNotification(OrderPlacedEvent orderPlacedEvent);
}
