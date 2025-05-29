package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.ProductoDTO;
import com.techmarket.productservice.model.entities.Category;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private ProductServiceImpl productService;

  private ObjectId categoryId;

  private ProductoDTO productDto;

  @BeforeEach
  void setUp() {
    categoryId = new ObjectId("507f191e810c19729de860ea");
    productDto = ProductoDTO.builder()
        .name("Shirt")
        .nameWithDetail("Cotton Shirt")
        .stock(20)
        .price(new BigDecimal("29.99"))
        .category("CLOTHING")
        .img("img-url.jpg")
        .build();
  }

  @Test
  void createProduct_shouldSaveProductWhenCategoryExists() {
    // Given
    Category category = new Category(categoryId, "CLOTHING", new Promotion());

    when(categoryRepository.findByName("CLOTHING")).thenReturn(Optional.of(category));

    // When
    productService.createProduct(productDto);

    // Then
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(productCaptor.capture());

    Product saved = productCaptor.getValue();
    assertEquals("Shirt", saved.getName());
    assertEquals("Cotton Shirt", saved.getNameWithDetail());
    assertEquals(categoryId, saved.getCategory());
    assertEquals(new BigDecimal("29.99"), saved.getPrice());
    assertEquals(20, saved.getStock());
    assertEquals("img-url.jpg", saved.getImg());
  }

  @Test
  void createProduct_shouldThrowExceptionWhenCategoryNotFound() {
    // Given
    ProductoDTO productDto = ProductoDTO.builder()
        .name("Shirt")
        .category("UNKNOWN")
        .build();

    when(categoryRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

    // When / Then
    assertThrows(NoSuchElementException.class, () -> productService.createProduct(productDto));
    verify(productRepository, never()).save(any());
  }

  @Test
  void getAllProducts_shouldReturnMappedDTOList() {
    // Given
    Product product = Product.builder()
        .name("Shoes")
        .nameWithDetail("Running Shoes")
        .stock(10)
        .price(new BigDecimal("49.99"))
        .category(categoryId)
        .img("shoes.jpg")
        .build();

    when(productRepository.findAll()).thenReturn(List.of(product));
    when(productMapper.mapToProductResponse(List.of(product))).thenReturn(List.of(productDto));

    // When
    List<ProductoDTO> result = productService.getAllProducts();

    // Then
    assertEquals(1, result.size());
    ProductoDTO dto = result.get(0);
    assertEquals("Shirt", dto.getName());
    assertEquals("Cotton Shirt", dto.getNameWithDetail());
    assertEquals("CLOTHING", dto.getCategory());
    assertEquals(20, dto.getStock());
    assertEquals(new BigDecimal("29.99"), dto.getPrice());
    assertEquals("img-url.jpg", dto.getImg());
  }
}
