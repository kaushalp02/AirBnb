package com.kaushal.projects.airBnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    private String city;

    @ElementCollection
    @CollectionTable(name = "hotel_photos", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "photo_url", columnDefinition = "TEXT")
    private Set<String> photos = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "amenity")
    private Set<String> amenities = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime changedDate;

    @Embedded
    private HotelContactInfo contactInfo;

}
