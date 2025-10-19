package com.kaushal.projects.airBnbApp.dto;

import com.kaushal.projects.airBnbApp.entity.HotelContactInfo;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {

    private long id;

    @NotBlank(message = "Name of the Hotel cannot be blank.")
    @Size(min = 5, message = "Hotel Name should be atleast 5 character long.")
    private String name;

    @NotBlank(message = "City cannot be blank.")
    private String city;

    private Set<String> photos = new HashSet<>();
    private Set<String> amenities = new HashSet<>();
    private LocalDateTime createdDate;
    private LocalDateTime changedDate;

    @Valid
    private HotelContactInfo contactInfo;
    private boolean active;
}
