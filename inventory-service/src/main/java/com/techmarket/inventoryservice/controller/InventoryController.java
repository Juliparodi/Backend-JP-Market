package com.techmarket.inventoryservice.controller;

import com.techmarket.inventoryservice.domain.dto.InventoryResponse;
import com.techmarket.inventoryservice.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Log4j2
public class InventoryController {

    private final IInventoryService inventoryService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam("skuCode") List<String> skuCode) {
        log.debug("Entering inventory service...");
        return inventoryService.isInStock(skuCode);
    }

}
