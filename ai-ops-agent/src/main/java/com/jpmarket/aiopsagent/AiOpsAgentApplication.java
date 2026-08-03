package com.jpmarket.aiopsagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AiOpsAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiOpsAgentApplication.class, args);
    }

}
