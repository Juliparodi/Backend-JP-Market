package com.techmarket.productservice.service;

import com.techmarket.productservice.exceptions.PromotionAlreadyExistException;
import com.techmarket.productservice.model.dto.PromotionDTO;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.repository.PromotionRepository;
import com.techmarket.productservice.service.impl.PromotionServiceImpl;
import com.techmarket.productservice.service.mapper.PromotionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PromotionServiceImplTest {

  @Mock
  private PromotionRepository promotionRepository;

  @Mock
  private PromotionMapper promotionMapper;

  @InjectMocks
  private PromotionServiceImpl promotionService;

  @Test
  void createPromotion_shouldSaveWhenNotExists() {
    PromotionDTO dto = PromotionDTO.builder()
        .promotionCode("WINTER2025")
        .name("Winter Promo")
        .description("20% off")
        .discountRate(new BigDecimal("20.1"))
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(10))
        .build();

    when(promotionRepository.findByPromotionCode("WINTER2025")).thenReturn(Optional.empty());
    when(promotionMapper.toEntity(dto)).thenReturn(Promotion.builder().promotionCode("WINTER2025").name("Winter Promo").build());

    promotionService.createPromotion(dto);

    ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
    verify(promotionRepository).save(captor.capture());

    Promotion saved = captor.getValue();
    assertEquals("WINTER2025", saved.getPromotionCode());
    assertEquals("Winter Promo", saved.getName());
  }

  @Test
  void createPromotion_shouldThrowIfAlreadyExists() {
    // given
    PromotionDTO dto = PromotionDTO.builder()
        .promotionCode("WINTER2025")
        .name("Winter Promo")
        .description("20% off")
        .discountRate(new BigDecimal("20.1"))
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(10))
        .build();

    when(promotionRepository.findByPromotionCode("WINTER2025")).thenReturn(Optional.of(new Promotion()));

    assertThrows(PromotionAlreadyExistException.class, () -> promotionService.createPromotion(dto));
    verify(promotionRepository, never()).save(any());
  }
}
