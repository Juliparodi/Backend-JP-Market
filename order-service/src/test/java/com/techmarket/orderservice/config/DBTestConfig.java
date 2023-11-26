package com.techmarket.orderservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class DBTestConfig {


    @Bean
    public MySQLContainer<?> mySQLContainer() {
        MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.26"))
                .withDatabaseName("defaultdb")
                .withUsername("avnadmin")
                .withPassword("AVNS_ViMLlH4Ah3IR23tl_T1");
        container.start();
        return container;
    }
}
