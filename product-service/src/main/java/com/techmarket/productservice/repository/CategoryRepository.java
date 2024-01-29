package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
    /*
    Use INSERT for new documents and save for UPDATES
     */
    Optional<Category> findByName(String phone);
    List<Category> findByNameLike(String name);
    List<Category> findByNameNotNull();
    List<Category> findByNameNull();

}
