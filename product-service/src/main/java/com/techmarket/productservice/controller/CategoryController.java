package com.techmarket.productservice.controller;

import com.techmarket.productservice.model.dto.CategoryDTO;
import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.model.dto.PromotionDTO;
import com.techmarket.productservice.service.ICategoryService;
import com.techmarket.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/techMarket/category")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody @Valid CategoryDTO categoryDTO) {
        categoryService.createCategory(categoryDTO);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDTO> getAllProducts() {
        return categoryService.getAllCategories();
    }
}
