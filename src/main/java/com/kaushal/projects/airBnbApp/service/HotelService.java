package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.dto.HotelInfoDto;

public interface HotelService {

    HotelDto createHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto replaceHotel(long id, HotelDto hotelDto);

    void deleteHotelById(long id);

    void activateHotel(long id);

    HotelInfoDto getHoteInfoById(Long hotelId);
}
