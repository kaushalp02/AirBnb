package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.RoomDto;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.Room;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.repository.HotelRepository;
import com.kaushal.projects.airBnbApp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;

    @Override
    public RoomDto createRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating new room for the hotel : {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+hotelId+" (While creating room for the hotel.)"));

        Room room = modelMapper.map(roomDto, Room.class);

        room.setHotel(hotel);

        room = roomRepository.save(room);
        log.info("Successfully Created room with id : {} for Hotel : {}", room.getId(),hotelId);

        //TODO create inventory for the room if the hotel is active
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
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with id : "+hotelId+" (While getting rooms for the hotel.)"));

        return hotel.getRooms().stream().map((element) -> modelMapper.map(element, RoomDto.class)).toList();

    }

    @Override
    public void deleteRoomById(Long id) {
        log.info("Start of deletion of room : {}", id);

        boolean exists = roomRepository.existsById(id);

        if (!exists)
        {
            throw new ResourceNotFoundException("Room Not Found with id : "+ id + "(While deleting a room)");
        }

        roomRepository.deleteById(id);
        log.info("Successfully delete the room : {}", id);
        //TOdO delete the future inventory for this room also
    }
}
