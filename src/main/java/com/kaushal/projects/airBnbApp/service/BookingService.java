package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.BookingDto;
import com.kaushal.projects.airBnbApp.dto.BookingRequestDto;
import com.kaushal.projects.airBnbApp.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequestDto bookingRequestDto);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestsDtoList);
}
