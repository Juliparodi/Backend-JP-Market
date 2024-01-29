package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, ObjectId>  {

    @Query("{'stock' : {$gte: ?0}}")
    List<Product> findByMinStock(int stock);
}
