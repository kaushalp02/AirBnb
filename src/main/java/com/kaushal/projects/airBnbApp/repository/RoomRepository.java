package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}