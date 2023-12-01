package com.techmarket.productservice.service.impl;

import com.techmarket.productservice.exceptions.PromotionAlreadyExistException;
import com.techmarket.productservice.model.dto.PromotionDTO;
import com.techmarket.productservice.model.entities.Product;
import com.techmarket.productservice.model.entities.Promotion;
import com.techmarket.productservice.repository.CategoryRepository;
import com.techmarket.productservice.repository.PromotionRepository;
import com.techmarket.productservice.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements IPromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public List<PromotionDTO> getAllPromotions() {
        return null;
    }

    @Override
    public void createPromotion(PromotionDTO promotionRequest) {
        if (promotionNotExist(promotionRequest)) {
            Promotion promotion = Promotion.builder()
                    .promotionCode(promotionRequest.getPromotionCode().toUpperCase())
                    .name(promotionRequest.getName())
                    .description(promotionRequest.getDescription())
                    .discountRate(promotionRequest.getDiscountRate())
                    .startDate(promotionRequest.getStartDate())
                    .endDate(promotionRequest.getEndDate())
                    .build();

            promotionRepository.save(promotion);
        } else {
            throw new PromotionAlreadyExistException();
        }
    }

    private boolean promotionNotExist(PromotionDTO promotionRequest) {
        Optional<Promotion> promotion = promotionRepository.findByPromotionCode(promotionRequest.getPromotionCode().toUpperCase());
        return promotion.isPresent();
    }
}
