package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.ProductDTO;
import com.techmarket.productservice.model.dto.ProductWithCategoryDTO;

import java.util.List;

public interface IProductService {
    List<ProductWithCategoryDTO> getAllProducts();

    void createProduct(ProductDTO productRequest);

    ProductWithCategoryDTO getProductById(String id);

    void updateProduct(String id, ProductDTO productRequest);

    void deleteProduct(String id);
}
