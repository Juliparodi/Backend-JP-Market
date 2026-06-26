package com.techmarket.inventoryservice.repository;

import com.techmarket.inventoryservice.domain.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class InitializeData {

	private final InventoryRepository inventoryRepository;

	@PostConstruct
	public void loadData() {
		inventoryRepository.deleteAll();

		List<String> productNames = List.of(
				"Iphone 13",
				"Iphone 12",
				"Iphone 11",
				"Samsung S22 Ultra",
				"Samsung A54",
				"Samsung A34",
				"Motorola 1",
				"Motorola 2",
				"Motorola 3",
				"Notebook Dell",
				"Notebook HP",
				"Notebook 3",
				"Notebook 4",
				"Headphones 1",
				"Headphones 2",
				"Headphones 3",
				"Headphones 4"
		);

		List<Inventory> inventories = new ArrayList<>();

		for (String name : productNames) {
			String sku = "SKU-" + name.toUpperCase().replace(" ", "-");
			Inventory inv = Inventory.builder()
					.skuCode(sku)
					.quantity(3)
					.build();
			inventories.add(inv);
		}

		inventoryRepository.saveAll(inventories);
	}

}
