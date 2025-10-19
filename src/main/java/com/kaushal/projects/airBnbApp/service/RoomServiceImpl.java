package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.RoomDto;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.Room;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.repository.HotelRepository;
import com.kaushal.projects.airBnbApp.repository.InventoryRepository;
import com.kaushal.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public RoomDto createRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating new room for the hotel : {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+hotelId+" (While creating room for the hotel.)"));

        Room room = modelMapper.map(roomDto, Room.class);

        room.setHotel(hotel);

        room = roomRepository.save(room);
        log.info("Successfully Created room with id : {} for Hotel : {}", room.getId(),hotelId);

        //create inventory for the room if the hotel is active
        if (hotel.isActive() )
        {
            inventoryService.initializeRoomInvForYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public RoomDto getRoomById(Long id) {
        log.info("Getting room with id : {}", id);
        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found with id : "+id));

        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all room for the hotel : {}", hotelId);
        Hotel hotel = hotelRepository
                .hotelWithRoomsById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+hotelId+" (While getting rooms for the hotel.)"));

        List<Room> rooms = hotel.getRooms();

        // If there are no rooms, we can return an empty list immediately.
        if (rooms == null || rooms.isEmpty()) {
            return Collections.emptyList();
        }

        //Get the photos and amenities for all the rooms
        roomRepository.findWithPhotosAndAmenitiesByRoomIn(rooms);

        return rooms.stream().map((element) -> modelMapper.map(element, RoomDto.class)).toList();

    }

    @Override
    @Transactional
    public void deleteRoomById(Long id) {
        log.info("Start of deletion of room : {}", id);

        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found with id : "+ id + "(While deleting a room)"));


        //Delete the inventory for the future for this room
        inventoryService.deleteFutureInventories(room);

        roomRepository.deleteById(id);
        log.info("Successfully delete the room : {}", id);


    }
}
