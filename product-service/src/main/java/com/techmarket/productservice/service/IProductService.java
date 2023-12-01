package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.ProductoDTO;

import java.util.List;

public interface IProductService {
    List<ProductoDTO> getAllProducts();

    void createProduct(ProductoDTO productRequest);
}
