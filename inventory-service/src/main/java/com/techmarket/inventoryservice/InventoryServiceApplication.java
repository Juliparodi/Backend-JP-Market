package com.techmarket.inventoryservice;

import com.techmarket.inventoryservice.domain.entities.Inventory;
import com.techmarket.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> inventoryRepository.saveAll(List.of(
				Inventory.builder()
						.skuCode("iphone_13")
						.quantity(100)
						.build(),
				Inventory.builder()
						.skuCode("iphone_13_red")
						.quantity(0)
						.build()));
	}

}
