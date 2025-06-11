package com.techmarket.productservice.controller;

import com.techmarket.productservice.model.dto.PromotionDTO;
import com.techmarket.productservice.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/techMarket/promotion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromotionController {

    private final IPromotionService promotionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody @Valid PromotionDTO promotionDTO) {
        promotionService.createPromotion(promotionDTO);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionDTO> getAllProducts() {
        return promotionService.getAllPromotions();
    }
}
