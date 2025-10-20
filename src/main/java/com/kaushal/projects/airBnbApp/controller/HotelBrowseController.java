package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.dto.HotelInfoDto;
import com.kaushal.projects.airBnbApp.dto.HotelSearchRequest;
import com.kaushal.projects.airBnbApp.service.HotelService;
import com.kaushal.projects.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;

    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest)
    {
        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable(name = "hotelId") Long hotelId)
    {
        return ResponseEntity.ok(hotelService.getHoteInfoById(hotelId));
    }
}
