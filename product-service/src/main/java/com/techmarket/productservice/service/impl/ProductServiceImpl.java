package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductoDTO productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .nameWithDetail(productRequest.getNameWithDetail())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .category(new ObjectId("10"))
                .img(productRequest.getImg())

                .build();

        productRepository.save(product);
    }

    public List<ProductoDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductoDTO mapToProductResponse(Product product) {
        return ProductoDTO.builder()
                .name(product.getName())
                .nameWithDetail(product.getNameWithDetail())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(String.valueOf(new ObjectId("20")))
                .img(product.getImg())
                .build();
    }
}
