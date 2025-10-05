package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.RoomDto;
import com.kaushal.projects.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/admin/hotel/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@PathVariable("hotelId") long id, @RequestBody RoomDto roomDto){
        RoomDto newRoom = roomService.createRoom(id, roomDto);
        return new ResponseEntity<>(newRoom,CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable("id") long id){
        RoomDto newRoom = roomService.getRoomById(id);
        return new ResponseEntity<>(newRoom,OK);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomByHotel(@PathVariable("hotelId") long hotelId){
        List<RoomDto> newRoom = roomService.getAllRoomsInHotel(hotelId);
        return new ResponseEntity<>(newRoom,OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") long id){
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }



}
