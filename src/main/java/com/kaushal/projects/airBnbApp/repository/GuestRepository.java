package com.kaushal.projects.airBnbApp.repository;
import com.kaushal.projects.airBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GuestRepository extends JpaRepository<Guest, Long> {
}