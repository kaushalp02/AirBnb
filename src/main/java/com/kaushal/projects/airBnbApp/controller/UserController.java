package com.kaushal.projects.airBnbApp.controller;

import com.kaushal.projects.airBnbApp.dto.BookingDto;
import com.kaushal.projects.airBnbApp.dto.UserDto;
import com.kaushal.projects.airBnbApp.dto.UserUpdateDto;
import com.kaushal.projects.airBnbApp.service.BookingService;
import com.kaushal.projects.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @PutMapping
    public ResponseEntity<Void> updateUser(@RequestBody UserUpdateDto userUpdateDto){
        userService.updateUser(userUpdateDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDto>> getUserBookings(){
        return ResponseEntity.ok(bookingService.gerUserBookings());
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserInfo()
    {
        return ResponseEntity.ok(userService.getUserInfo());
    }

}
