package com.jpmarket.aiopsagent.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "jpmarket")
@Data
public class ServicesProperties {

    private Map<String, Service> services = new HashMap<>();

    @Getter
    @Setter
    public static class Service {
        private String baseUrl;
    }
}
