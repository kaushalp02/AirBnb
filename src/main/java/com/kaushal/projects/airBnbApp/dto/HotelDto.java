package com.kaushal.projects.airBnbApp.dto;

import com.kaushal.projects.airBnbApp.entity.HotelContactInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {

    private long id;
    private String name;
    private String city;
    private Set<String> photos = new HashSet<>();
    private Set<String> amenities = new HashSet<>();
    private LocalDateTime createdDate;
    private LocalDateTime changedDate;
    private HotelContactInfo contactInfo;
    private boolean active;
}
