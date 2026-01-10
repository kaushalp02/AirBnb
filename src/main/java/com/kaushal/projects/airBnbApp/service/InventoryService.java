package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.dto.HotelSearchRequest;
import com.kaushal.projects.airBnbApp.dto.InventoryDto;
import com.kaushal.projects.airBnbApp.dto.UpdateInventoryRequestDto;
import com.kaushal.projects.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomInvForYear(Room room);

    void deleteFutureInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getInventoryByRoom(long roomId);

    void updateInventory(long roomId, UpdateInventoryRequestDto inventoryRequestDto);
}
