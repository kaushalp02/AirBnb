package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.BookingDto;
import com.kaushal.projects.airBnbApp.dto.BookingRequestDto;
import com.kaushal.projects.airBnbApp.dto.GuestDto;
import com.kaushal.projects.airBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequestDto bookingRequestDto){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequestDto));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,  @RequestBody List<GuestDto> guestsDtoList)
    {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestsDtoList));
    }
}
