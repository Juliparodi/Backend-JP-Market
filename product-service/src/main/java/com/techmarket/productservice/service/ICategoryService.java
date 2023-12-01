package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.CategoryDTO;
import com.techmarket.productservice.model.dto.ProductoDTO;

import java.util.List;

public interface ICategoryService {

    List<CategoryDTO> getAllCategories();

    void createCategory(CategoryDTO categoryRequest);
}
