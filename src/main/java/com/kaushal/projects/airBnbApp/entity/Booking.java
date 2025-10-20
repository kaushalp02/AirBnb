package com.kaushal.projects.airBnbApp.entity;

import com.kaushal.projects.airBnbApp.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer roomCount;

    @Column(nullable = false)
    private LocalDate checkinDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "guest_booking",
            joinColumns =  @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    private Set<Guest> guests;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
