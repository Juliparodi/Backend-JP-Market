package com.techmarket.productservice.model.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Variation {
    public String color;
    public String size;
}
