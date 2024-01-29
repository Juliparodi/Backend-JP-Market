package com.techmarket.productservice.config;

import com.techmarket.productservice.model.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenericMongoAuditListener extends AbstractMongoEventListener<Product> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Product> event) {
        Object obj = event.getSource();
        log.info("updated " + obj);
    }
}
