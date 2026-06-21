package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Category;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InitializeDataTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private PromotionRepository promotionRepository;

  @InjectMocks
  private InitializeData initializeData;

  @BeforeEach
  void setup() {
    Category headphones = Category.builder().id(new ObjectId("0123456789abcdefabcdef12")).name("headphones").build();

    when(categoryRepository.save(any())).thenReturn(headphones);

  }

  @Test
  void shouldLoadInitialData() {
    initializeData.loadData();

    verify(productRepository).deleteAll();
    verify(categoryRepository).deleteAll();

    verify(categoryRepository, times(3)).save(any(Category.class));
    verify(productRepository).saveAll(anyList());

  }


}
