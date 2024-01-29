package com.techmarket.productservice.config;

import com.techmarket.productservice.model.entities.Promotion;
import org.bson.json.StrictJsonWriter;
import org.springframework.core.convert.converter.Converter;


public class PromotionWriteConverter implements Converter<Promotion, String> {
    @Override
    public String convert(Promotion promotion) {
        return promotion.getPromotionCode() + ", "
                + promotion.getDescription();
    }
}
