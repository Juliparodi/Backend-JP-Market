package com.techmarket.productservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductRequestDTO {
    private String name;
    private String nameWithDetail;
    private String category;
    private Integer stock;
    private String img;
    private BigDecimal price;
}