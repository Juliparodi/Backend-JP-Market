package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.ProductRequestDTO;
import com.techmarket.productservice.model.entities.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();

    void createProduct(ProductRequestDTO productRequest);
}
