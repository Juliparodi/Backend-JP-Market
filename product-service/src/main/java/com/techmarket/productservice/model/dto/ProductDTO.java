package com.techmarket.productservice.model.dto;

import com.techmarket.productservice.model.entities.Variation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String name;
    private String nameWithDetail;
    private String category;
    private Integer stock;
    private String img;
    private BigDecimal price;
    private List<Variation> variations;
    private String categoryName;
    private String skuCode;
}