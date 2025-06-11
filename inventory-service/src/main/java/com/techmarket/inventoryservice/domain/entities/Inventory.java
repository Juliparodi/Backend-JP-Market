package com.techmarket.inventoryservice.domain.entities;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "t_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "sku_code")
    String skuCode;

    Integer quantity;

}
