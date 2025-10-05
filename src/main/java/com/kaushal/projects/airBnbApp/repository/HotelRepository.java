package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}