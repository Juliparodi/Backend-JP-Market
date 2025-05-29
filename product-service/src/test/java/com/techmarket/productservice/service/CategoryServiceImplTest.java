package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
import com.techmarket.productservice.service.impl.CategoryServiceImpl;
import com.techmarket.productservice.service.impl.ProductServiceImpl;
import com.techmarket.productservice.service.mapper.ProductMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  @Test
  void assertNullInAllCategories(){
    assertNull(categoryService.getAllCategories());
  }

}
