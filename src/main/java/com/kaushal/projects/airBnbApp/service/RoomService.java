package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.RoomDto;
import com.kaushal.projects.airBnbApp.entity.Room;

import java.util.List;

public interface RoomService {

    RoomDto createRoom(Long hotelId, RoomDto roomDto);

    RoomDto getRoomById(Long id);

    List<RoomDto> getAllRoomsInHotel(Long hotelId);

    void deleteRoomById(Long id);
}


