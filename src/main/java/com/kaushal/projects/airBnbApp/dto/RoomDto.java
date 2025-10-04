package com.kaushal.projects.airBnbApp.dto;

import com.kaushal.projects.airBnbApp.entity.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

    private long id;
    private String type;
    private BigDecimal basePrice;
    private Set<String> photos = new HashSet<>();
    private Set<String> amenities = new HashSet<>();
    private LocalDateTime createdDate;
    private LocalDateTime changedDate;
    private Integer totalCount;
    private Integer capacity;
    private Boolean active;
}
