package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.exceptions.CategoryNotFoundException;
import com.techmarket.productservice.exceptions.ProductNotFoundException;
import com.techmarket.productservice.model.dto.ProductDTO;
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


    public void createProduct(ProductDTO productRequest) {
        Optional<Category> categoryOptional = categoryRepository.findByName(productRequest.getCategory());

        Product product = Product.builder()
                .name(productRequest.getName())
                .nameWithDetail(productRequest.getNameWithDetail())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .category(categoryOptional.map(Category::getId).orElseThrow(RuntimeException::new))
                .img(productRequest.getImg())
                .build();

        log.info("product saved");
        productRepository.save(product);
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return productMapper.mapToProductResponse(products);
    }

    @Override
    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.mapToProductResponse(List.of(product)).get(0);
    }

    @Override
    public void updateProduct(String id, ProductDTO productRequest) {
        Product existingProduct = productRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        Optional<Category> categoryOptional = categoryRepository.findByName(productRequest.getCategory());

        existingProduct.setName(productRequest.getName());
        existingProduct.setNameWithDetail(productRequest.getNameWithDetail());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setStock(productRequest.getStock());
        existingProduct.setCategory(categoryOptional.orElseThrow(() -> new CategoryNotFoundException("Category not found: " + productRequest.getCategory())).getId());
        existingProduct.setImg(productRequest.getImg());

        productRepository.save(existingProduct);
        log.info("product updated");
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(new ObjectId(id))) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(new ObjectId(id));
        log.info("product deleted");
    }
}
