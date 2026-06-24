package com.techmarket.productservice.controller;

import com.techmarket.productservice.model.dto.ProductDTO;
import com.techmarket.productservice.model.dto.ProductWithCategoryDTO;
import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.service.IProductService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private IProductService productService;

  @Autowired
  private JsonMapper jsonMapper;

  @Test
  @DisplayName("POST /api/product - should create product")
  void createProduct_shouldReturnCreated() throws Exception {
    ProductDTO productRequest = new ProductDTO();
    productRequest.setName("Laptop");
    productRequest.setPrice(new BigDecimal("1299.99"));

    doNothing().when(productService).createProduct(Mockito.any(ProductDTO.class));

    mockMvc.perform(post("/api/product")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsString(productRequest)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("GET /api/product/all - should return list of products")
  void getAllProducts_shouldReturnProductList() throws Exception {


    Category category = Category.builder().id(new ObjectId()).name("Smartphones").build();
    List<ProductWithCategoryDTO> products = List.of(
            new ProductWithCategoryDTO(
                    new ObjectId().toHexString(),
                    "iPhone 16",
                    "iPhone 16 Pro Max 256GB",
                    category,
                    10,
                    "iphone.jpg",
                    new BigDecimal("1299.99")
            ));

    when(productService.getAllProducts()).thenReturn(products);

    mockMvc.perform(get("/api/product/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(products.size()))
        .andExpect(jsonPath("$[0].name").value("iPhone 16"));
  }

}
