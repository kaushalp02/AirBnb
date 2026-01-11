package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.BookingDto;
import com.kaushal.projects.airBnbApp.dto.BookingRequestDto;
import com.kaushal.projects.airBnbApp.dto.GuestDto;
import com.kaushal.projects.airBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<BookingDto> addGuests(@PathVariable(name = "bookingId") Long bookingId,  @RequestBody List<GuestDto> guestsDtoList)
    {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestsDtoList));
    }

    @PostMapping("{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable(name = "bookingId") Long bookingId){
        String sessionUrl = bookingService.initiateBooking(bookingId);

        return ResponseEntity.ok(Map.of("SessionUrl", sessionUrl));
    }

    @PostMapping("{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable(name = "bookingId") Long bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{bookingId}/status")
    public ResponseEntity<Map<String, String>> getBookingStatus(@PathVariable(name = "bookingId") Long bookingId){
        return ResponseEntity.ok(Map.of("status",bookingService.getBookingStatus(bookingId)));
    }
}
