package com.techmarket.productservice.service.mapper;

import com.techmarket.productservice.model.dto.PromotionDTO;
import com.techmarket.productservice.model.entities.Promotion;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PromotionMapper {

  Promotion toEntity(PromotionDTO promotionDTO);
}
