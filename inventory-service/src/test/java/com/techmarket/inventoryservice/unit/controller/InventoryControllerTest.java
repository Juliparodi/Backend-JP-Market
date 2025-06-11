package com.techmarket.inventoryservice.unit.controller;

import com.techmarket.inventoryservice.controller.InventoryController;
import com.techmarket.inventoryservice.domain.dto.InventoryResponse;
import com.techmarket.inventoryservice.repository.InventoryRepository;
import com.techmarket.inventoryservice.service.IInventoryService;
import com.techmarket.inventoryservice.utils.JsonConverter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)  // Loads only the controller layer
public class InventoryControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private IInventoryService inventoryService;
  @MockitoBean
  private InventoryRepository inventoryRepository;


  @Test
  @SneakyThrows
  void whenExecutingGetInventoryWithSkuCodeParam_thenRespondWithInventoryResponse() {
    List<String> skuCodes = List.of("iphone_13");
    when(inventoryRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
    when(inventoryService.isInStock(skuCodes)).thenReturn(List.of(
        InventoryResponse.builder()
            .skuCode("123")
            .isInStock(false)
            .build(),
        InventoryResponse.builder()
            .skuCode("1234")
            .isInStock(true)
            .build()
    ));

    mockMvc.perform(get("/api/techMarket/inventory?skuCode=iphone_13"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(JsonConverter.loadJsonFromFile("inventoryResponse.json")));
  }

}
