package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.model.dto.ProductRequestDTO;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequestDTO productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .nameWithDetail(productRequest.getNameWithDetail())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .img(productRequest.getImg())

                .build();

        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private Product mapToProductResponse(Product product) {
        return Product.builder()
                .id(product.getId())
                .name(product.getName())
                .nameWithDetail(product.getNameWithDetail())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(product.getCategory())
                .img(product.getImg())
                .build();
    }
}
