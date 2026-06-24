package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, ObjectId>,
        ProductCustomRepository  {
}
