package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.dto.HotelSearchRequest;
import com.kaushal.projects.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomInvForYear(Room room);

    void deleteFutureInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
