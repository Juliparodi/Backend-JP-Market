package com.techmarket.productservice.service;

import com.techmarket.productservice.model.dto.PromotionDTO;

import java.util.List;

public interface IPromotionService {

    List<PromotionDTO> getAllPromotions();

    void createPromotion(PromotionDTO promotionDTO);
}
