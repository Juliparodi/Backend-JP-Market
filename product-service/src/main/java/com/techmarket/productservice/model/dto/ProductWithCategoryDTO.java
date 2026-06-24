package com.techmarket.productservice.model.dto;

import com.techmarket.productservice.model.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ProductWithCategoryDTO {

    private String id;

    private String name;

    private String nameWithDetail;

    private Category category;

    private Integer stock;

    private String img;

    private BigDecimal price;

}
