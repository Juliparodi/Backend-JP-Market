package com.jpmarket.aiopsagent.domain.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class ServiceNameTest {

    @ParameterizedTest(name = "input={0} -> expected={1}")
    @CsvSource({
        "ORDER,   ORDER",
        "order,   ORDER",
        "order-service, ORDER",
        "PRODUCT, PRODUCT",
        "product-service, PRODUCT",
        "inventory, INVENTORY",
        "notification, NOTIFICATION",
        "gateway, GATEWAY"
    })
    void from_shouldResolveAllValidVariants(String input, String expected) {
        ServiceName result = ServiceName.from(input.trim());
        assertThat(result).isEqualTo(ServiceName.valueOf(expected.trim()));
    }

    @Test
    void from_shouldThrowForUnknownService() {
        assertThatThrownBy(() -> ServiceName.from("unknown-service"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown service: unknown-service");
    }

    @Test
    void getPropertyName_shouldReturnLowerCaseName() {
        assertThat(ServiceName.ORDER.getPropertyName()).isEqualTo("order");
        assertThat(ServiceName.PRODUCT.getPropertyName()).isEqualTo("product");
        assertThat(ServiceName.INVENTORY.getPropertyName()).isEqualTo("inventory");
        assertThat(ServiceName.NOTIFICATION.getPropertyName()).isEqualTo("notification");
        assertThat(ServiceName.GATEWAY.getPropertyName()).isEqualTo("gateway");
    }
}
