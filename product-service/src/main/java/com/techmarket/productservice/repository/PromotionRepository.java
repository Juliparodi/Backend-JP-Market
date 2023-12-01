package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Promotion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PromotionRepository extends MongoRepository<Promotion, ObjectId> {
    Optional<Promotion> findByPromotionCode(String promotionCode);
}
