package com.techmarket.productservice.service.mapper;

import com.techmarket.productservice.model.dto.ProductDTO;
import com.techmarket.productservice.model.entities.Product;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "category", source = "category")
  List<ProductDTO> mapToProductResponse(List<Product> product);

  @Mapping(target = "category", source = "category")
  Product requestToEntity(ProductDTO product);

  default String map(ObjectId objectId) {
    return objectId != null ? objectId.toHexString() : null;
  }

  default ObjectId map(String objectId) {
    return objectId != null ? new ObjectId(objectId) : null;
  }

}
