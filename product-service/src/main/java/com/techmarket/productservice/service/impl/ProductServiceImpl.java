package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.exceptions.CategoryNotFoundException;
import com.techmarket.productservice.exceptions.ProductNotFoundException;
import com.techmarket.productservice.model.dto.ProductDTO;
import com.techmarket.productservice.model.dto.ProductWithCategoryDTO;
import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @CacheEvict(value = "products", allEntries = true)
    public void createProduct(ProductDTO productRequest) {
        Category category =
                categoryRepository
                        .findByName(productRequest.getCategory())
                        .orElseThrow(
                                () -> new CategoryNotFoundException(
                                        "Category not found"
                                )
                        );

        Product product =
                Product.builder()
                        .name(productRequest.getName())
                        .nameWithDetail(productRequest.getNameWithDetail())
                        .price(productRequest.getPrice())
                        .stock(productRequest.getStock())
                        .category(category.getId())
                        .img(productRequest.getImg())
                        .build();

        productRepository.save(product);
    }

    @Cacheable("products")
    public List<ProductWithCategoryDTO> getAllProducts() {
        return productRepository.findAllWithCategory();
    }

    @Override
    public ProductWithCategoryDTO  getProductById(String id) {
        ProductWithCategoryDTO product =
                productRepository.findByIdWithCategory(id);

        if (product == null) {
            throw new ProductNotFoundException(
                    "Product not found"
            );
        }

        return product;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
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
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(String id) {
        if (!productRepository.existsById(new ObjectId(id))) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(new ObjectId(id));
        log.info("product deleted");
    }
}
