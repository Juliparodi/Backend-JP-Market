package com.techmarket.productservice.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(value = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {

    @Id
    private ObjectId id;
    private String name;
    private String nameWithDetail;
    private String category;
    private Integer stock;
    private String img;
    private BigDecimal price;
    @CreatedDate
    private LocalDate creationDate;
    @LastModifiedDate
    private LocalDate modifiedDate;
}
