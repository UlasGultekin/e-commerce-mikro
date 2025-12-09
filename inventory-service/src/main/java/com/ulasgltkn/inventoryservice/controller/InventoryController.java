package com.ulasgltkn.inventoryservice.controller;

import com.ulasgltkn.inventoryservice.dto.AdjustInventoryRequest;
import com.ulasgltkn.inventoryservice.dto.InventoryDto;
import com.ulasgltkn.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    // 1. GET /inventory/{productId}
    @GetMapping("/{productId}")
    public InventoryDto getInventory(@PathVariable String productId) {
        return inventoryService.getInventory(productId);
    }

    // 2. POST /inventory/adjust
    @PostMapping("/adjust")
    @ResponseStatus(HttpStatus.OK)
    public InventoryDto adjustInventory(@Valid @RequestBody AdjustInventoryRequest request) {
        return inventoryService.adjustInventory(request);
    }
}
