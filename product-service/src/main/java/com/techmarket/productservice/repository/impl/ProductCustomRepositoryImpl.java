package com.techmarket.productservice.repository.impl;

import com.techmarket.productservice.model.dto.ProductWithCategoryDTO;
import com.techmarket.productservice.repository.ProductCustomRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl
        implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ProductWithCategoryDTO> findAllWithCategory() {

        Aggregation aggregation =
                newAggregation(
                        lookup(
                                "category",
                                "category",
                                "_id",
                                "category"
                        ),
                        unwind("category")
                );

        return mongoTemplate
                .aggregate(
                        aggregation,
                        "product",
                        ProductWithCategoryDTO.class
                )
                .getMappedResults();
    }

    @Override
    public ProductWithCategoryDTO findByIdWithCategory(String id) {

        Aggregation aggregation =
                newAggregation(
                        match(
                                org.springframework.data.mongodb.core.query.Criteria
                                        .where("_id")
                                        .is(new ObjectId(id))
                        ),
                        lookup(
                                "category",
                                "category",
                                "_id",
                                "category"
                        ),
                        unwind("category")
                );

        return mongoTemplate
                .aggregate(
                        aggregation,
                        "product",
                        ProductWithCategoryDTO.class
                )
                .getUniqueMappedResult();
    }
}