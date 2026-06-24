package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.dto.ProductWithCategoryDTO;

import java.util.List;

public interface ProductCustomRepository {

    List<ProductWithCategoryDTO> findAllWithCategory();

    ProductWithCategoryDTO findByIdWithCategory(String id);
}
