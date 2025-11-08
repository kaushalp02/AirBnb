package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.dto.HotelPriceDto;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    @Query("""
            SELECT com.kaushal.projects.airBnbApp.dto(h.hotel, AVG(h.price))
            FROM HotelMinPrice h
            WHERE h.hotel.city = :city
                AND h.date BETWEEN :startDate AND :endDate 
                AND h.hotel.active = true
            GROUP BY h.hotel
            """)
    Page<HotelPriceDto> findHotelWithMinPrice(
            @Param("city") String city,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
            );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}