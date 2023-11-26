package com.techmarket.inventoryservice.domain.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "t_inventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private Integer quantity;

}
