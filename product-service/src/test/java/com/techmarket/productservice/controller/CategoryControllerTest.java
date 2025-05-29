package com.techmarket.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.productservice.model.dto.CategoryDTO;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.service.ICategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ICategoryService categoryService;

  @MockitoBean
  private CategoryRepository categoryRepository;

  @MockitoBean
  private ProductRepository productRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/techMarket/category - should create category")
  void createProduct_shouldReturnCreated() throws Exception {
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setName("Electronics");

    doNothing().when(categoryService).createCategory(Mockito.any(CategoryDTO.class));

    mockMvc.perform(post("/api/techMarket/category")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDTO)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("GET /api/techMarket/category/all - should return all categories")
  void getAllProducts_shouldReturnCategoryList() throws Exception {
    List<CategoryDTO> categories = List.of(
        new CategoryDTO("Electronics"),
        new CategoryDTO("Books")
    );

    when(categoryService.getAllCategories()).thenReturn(categories);

    mockMvc.perform(get("/api/techMarket/category/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(categories.size()))
        .andExpect(jsonPath("$[0].name").value("Electronics"))
        .andExpect(jsonPath("$[1].name").value("Books"));
  }

}
