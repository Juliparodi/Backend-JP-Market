package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends MongoRepository<Product, ObjectId>  {
}
