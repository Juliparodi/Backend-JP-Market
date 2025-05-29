package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.service.IProductService;
import com.techmarket.productservice.service.mapper.ProductMapper;
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
    private final ProductMapper productMapper;


    public void createProduct(ProductoDTO productRequest) {
        Optional<Category> categoryOptional = categoryRepository.findByName(productRequest.getCategory());

        Product product = Product.builder()
                .name(productRequest.getName())
                .nameWithDetail(productRequest.getNameWithDetail())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .category(categoryOptional.map(Category::getId).orElse(null))
                .img(productRequest.getImg())
                .build();

        log.info("product saved");
        productRepository.save(product);
    }

    public List<ProductoDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return productMapper.mapToProductResponse(products);
    }

}
