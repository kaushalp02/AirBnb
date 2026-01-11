package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.BookingDto;
import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.dto.HotelReportDto;
import com.kaushal.projects.airBnbApp.service.BookingService;
import com.kaushal.projects.airBnbApp.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

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

    //Get all the hotels
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels()
    {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    //Get all the bookings for a hotel
    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingDto>> getBookingsByHotel(@PathVariable("id") Long id)
    {
        return ResponseEntity.ok(bookingService.getBookingsByHotel(id));
    }

    //report dashboard api
    @GetMapping("/{id}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable("id") Long id,
                                                         @RequestParam(name = "startDate", required = false)LocalDate startDate,
                                                         @RequestParam(name = "endDate",required = false)LocalDate endDate)
    {
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(id, startDate, endDate));
    }
}

