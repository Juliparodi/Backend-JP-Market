package com.techmarket.productservice.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Promotion {

    @Id
    private ObjectId id;
    private String promotionCode;
    private String name;
    private String description;
    private BigDecimal discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
}
