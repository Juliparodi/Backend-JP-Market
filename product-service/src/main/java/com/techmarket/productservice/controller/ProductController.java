package com.techmarket.productservice.controller;

import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/techMarket/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final IProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody @Valid ProductoDTO productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductoDTO> getAllProducts() {
        return productService.getAllProducts();
    }

}
