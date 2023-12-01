package com.techmarket.productservice.model.dto;

import com.techmarket.productservice.model.entities.Variation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductoDTO {
    private String name;
    private String nameWithDetail;
    private String category;
    private Integer stock;
    private String img;
    private BigDecimal price;
    private List<Variation> variations;
    private String categoryName;
}