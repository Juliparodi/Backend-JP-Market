package com.techmarket.productservice.controller;

import com.techmarket.productservice.model.dto.ProductRequestDTO;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/techMarket/product")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody @Valid ProductRequestDTO productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

}
