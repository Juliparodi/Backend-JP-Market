package com.techmarket.productservice.repository;

import com.techmarket.productservice.model.entities.Category;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    Category phone = Category.builder().id(new ObjectId("5f2d7c3b1e2f4a6b8c9d0e1f")).name("phone").build();
    Category notebook = Category.builder().id(new ObjectId("abcdefabcdefabcdefabcdef")).name("notebook").build();
    Category headphones = Category.builder().id(new ObjectId("0123456789abcdefabcdef12")).name("headphones").build();

    when(categoryRepository.findByName("phone")).thenReturn(Optional.of(phone));
    when(categoryRepository.findByName("notebook")).thenReturn(Optional.of(notebook));
    when(categoryRepository.findByName("headphones")).thenReturn(Optional.of(headphones));
  }

  @Test
  void shouldLoadInitialData() {
    initializeData.loadDate();

    verify(productRepository).deleteAll();
    verify(categoryRepository).deleteAll();

    verify(categoryRepository).saveAll(anyList());
    verify(productRepository).saveAll(anyList());

    verify(categoryRepository).findByName("phone");
    verify(categoryRepository).findByName("notebook");
    verify(categoryRepository).findByName("headphones");
  }


}
