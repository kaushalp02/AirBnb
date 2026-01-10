package com.kaushal.projects.airBnbApp.repository;

import com.kaushal.projects.airBnbApp.entity.Booking;
import com.kaushal.projects.airBnbApp.entity.Hotel;
import com.kaushal.projects.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelIdAndCreatedDateBetween(long hotelId, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findByUser(User user);
}

