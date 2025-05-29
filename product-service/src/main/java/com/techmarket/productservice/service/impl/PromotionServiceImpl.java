package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.exceptions.PromotionAlreadyExistException;
import com.techmarket.productservice.model.dto.PromotionDTO;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.repository.PromotionRepository;
import com.techmarket.productservice.service.IPromotionService;
import com.techmarket.productservice.service.mapper.PromotionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements IPromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;


    @Override
    public List<PromotionDTO> getAllPromotions() {
        return null;
    }

    @Override
    public void createPromotion(PromotionDTO promotionRequest) {
        if (getOptionalPromotion(promotionRequest).isPresent()) {
            throw new PromotionAlreadyExistException();
        }

        promotionRepository.save(promotionMapper.toEntity(promotionRequest));
    }

    private Optional<Promotion> getOptionalPromotion(PromotionDTO promotionRequest) {
        return promotionRepository.findByPromotionCode(promotionRequest.getPromotionCode().toUpperCase());
    }
}
