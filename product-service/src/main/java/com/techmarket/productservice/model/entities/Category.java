package com.techmarket.productservice.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/*
Useful annotations:
    Document: Table
    Id: Primary key.
    Field: Properties of the class
    Transient: That property of the class will NOT be persisted
    Indexed: To speed up if this property will be queries frequently. You can specify if this is unique or also the direction of the index (ASC or DESC)
    TextIndexed: fields in a full text search
    CompoundIndex: Creates an index for multiple fields (def= "{'name': 1 (ASC), 'promotion': -1 (DESC)})
    DbRef:  Adds a JOIN (you can have another Document embebbed here, same as relationships in RDB)


    Converter:
    If u want for example object Promotion to be persistid as whole String you have to use Converter
 */
@Document(value = "category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Category implements Serializable {

    @Id
    private ObjectId id;
    @Indexed
    private String name;
    private Promotion promotion;
}
