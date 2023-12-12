package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    public void createProduct(ProductoDTO productRequest) {
        Optional<Category> categoryOptional = categoryRepository.findByName(productRequest.getCategory());

        Product product = Product.builder()
                .name(productRequest.getName())
                .nameWithDetail(productRequest.getNameWithDetail())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .category(categoryOptional.get().getId())
                .img(productRequest.getImg())
                .build();

        log.info("product saved");
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
                .category(product.getCategory().toHexString())
                .img(product.getImg())
                .build();
    }
}
