package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@Valid @RequestBody HotelDto hotelDto){
        HotelDto newHotel = hotelService.createHotel(hotelDto);
        return new ResponseEntity<>(newHotel, CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable("id") Long id){
        HotelDto hotelDto = hotelService.getHotelById(id);
        return new ResponseEntity<>(hotelDto,OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> replaceHotelById(@Valid @PathVariable("id") long id, @RequestBody HotelDto hotelDto){
        HotelDto updatedHotel = hotelService.replaceHotel(id, hotelDto);
        return new ResponseEntity<>(updatedHotel, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable("id") Long id){
        hotelService.deleteHotelById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> activateHotel(@PathVariable("id") Long id) {
        hotelService.activateHotel(id);
        return ResponseEntity.noContent().build();
    }
}

