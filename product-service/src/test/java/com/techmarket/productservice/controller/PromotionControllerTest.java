package com.techmarket.productservice.controller;

import com.techmarket.productservice.model.dto.PromotionDTO;
import com.techmarket.productservice.service.IPromotionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromotionController.class)
public class PromotionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private IPromotionService promotionService;

  @Autowired
  private JsonMapper jsonMapper;

  @Test
  @DisplayName("POST /api/promotion - should create promotion")
  void createPromotion_shouldReturnCreated() throws Exception {
    PromotionDTO promo = new PromotionDTO();

    doNothing().when(promotionService).createPromotion(Mockito.any(PromotionDTO.class));

    mockMvc.perform(post("/api/promotion")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsString(promo)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("GET /api/promotion/all - should return all promotions")
  void getAllPromotions_shouldReturnList() throws Exception {
    List<PromotionDTO> promotions = List.of(
        new PromotionDTO()
    );

    when(promotionService.getAllPromotions()).thenReturn(promotions);

    mockMvc.perform(get("/api/promotion/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(promotions.size()));
  }
}
