package com.kaushal.projects.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelInfoDto {

    private HotelDto hotelDto;
    private List<RoomDto> rooms;

}
