package com.techmarket.productservice.controller;

import com.techmarket.productservice.model.dto.ProductDTO;
import com.techmarket.productservice.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Product Controller", description = "Controller for managing products")
public class ProductController {

    private final IProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product", description = "Adds a new product to the system")
    public void createProduct(@RequestBody @Valid ProductDTO productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    public ProductDTO getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update product", description = "Updates an existing product")
    public void updateProduct(@PathVariable String id, @RequestBody @Valid ProductDTO productRequest) {
        productService.updateProduct(id, productRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
