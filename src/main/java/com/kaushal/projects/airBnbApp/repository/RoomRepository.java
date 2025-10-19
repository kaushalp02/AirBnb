package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.photos LEFT JOIN FETCH r.amenities WHERE r IN :rooms")
    Optional<List<Room>> findWithPhotosAndAmenitiesByRoomIn(@Param("rooms") List<Room> rooms);
}