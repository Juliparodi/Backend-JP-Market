package com.techmarket.productservice.config;

import com.techmarket.productservice.model.entities.Promotion;
import org.springframework.core.convert.converter.Converter;

public class PromotionReadConverter implements Converter<String, Promotion> {


    @Override
    public Promotion convert(String source) {
        String[] parts = source.split(",");
   //     return new Promotion(source[0], source[1]);
        return Promotion.builder()
                .promotionCode(parts[0])
                .description(parts[1])
                .build();
    }
}
