package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.InventoryDto;
import com.kaushal.projects.airBnbApp.dto.UpdateInventoryRequestDto;
import com.kaushal.projects.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getInventoryByRoom(@PathVariable("roomId") long roomId)
    {
        return ResponseEntity.ok(inventoryService.getInventoryByRoom(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable(name = "roomId") long roomId,
                                                @RequestBody UpdateInventoryRequestDto inventoryRequestDto)
    {
        inventoryService.updateInventory(roomId, inventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
