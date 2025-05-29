package com.techmarket.productservice;

import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.model.entities.Variation;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.repository.PromotionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableEurekaClient
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
