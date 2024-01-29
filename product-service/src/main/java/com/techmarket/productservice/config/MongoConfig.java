package com.techmarket.productservice.config;

import com.github.mongobee.Mongobee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(new PromotionReadConverter());
        converters.add(new PromotionWriteConverter());

        return new MongoCustomConversions(converters);
    }

    @Bean
    public Mongobee mongobee() {
        Mongobee mongobee = new Mongobee(mongoUri);
        mongobee.setChangeLogsScanPackage("com.techmarket.productservice.db.migrations");
        mongobee.setEnabled(true);
        return mongobee;
    }


}
