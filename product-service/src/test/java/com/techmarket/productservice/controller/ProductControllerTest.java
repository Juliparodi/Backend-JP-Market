package com.techmarket.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.model.entities.Variation;
import com.techmarket.productservice.service.IProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private IProductService productService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/techMarket/product - should create product")
  void createProduct_shouldReturnCreated() throws Exception {
    ProductoDTO productRequest = new ProductoDTO();
    productRequest.setName("Laptop");
    productRequest.setPrice(new BigDecimal("1299.99"));

    doNothing().when(productService).createProduct(Mockito.any(ProductoDTO.class));

    mockMvc.perform(post("/api/techMarket/product")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("GET /api/techMarket/product/all - should return list of products")
  void getAllProducts_shouldReturnProductList() throws Exception {
    List<ProductoDTO> products = List.of(
        new ProductoDTO(
            "T-Shirt",
            "T-Shirt - Cotton - Red",
            "CLOTHING",
            50,
            "https://example.com/img.jpg",
            new BigDecimal("19.99"),
            List.of(Variation.builder().build()),
            "Men's Clothing"
    ));

    when(productService.getAllProducts()).thenReturn(products);

    mockMvc.perform(get("/api/techMarket/product/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(products.size()))
        .andExpect(jsonPath("$[0].name").value("T-Shirt"));
  }

}
