package com.techmarket.inventoryservice.domain.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "t_inventory")
@Value
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String skuCode;
    Integer quantity;

}
