package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.dto.HotelDto;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("SELECT DISTINCT h FROM Hotel h" +
            " JOIN FETCH h.rooms r" +
          " WHERE h.id = :hotelId")
    Optional<Hotel> hotelWithRoomsById(Long hotelId);
}