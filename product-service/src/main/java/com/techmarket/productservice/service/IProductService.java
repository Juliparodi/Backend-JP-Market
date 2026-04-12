package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.ProductDTO;

import java.util.List;

public interface IProductService {
    List<ProductDTO> getAllProducts();

    void createProduct(ProductDTO productRequest);

    ProductDTO getProductById(String id);

    void updateProduct(String id, ProductDTO productRequest);

    void deleteProduct(String id);
}
