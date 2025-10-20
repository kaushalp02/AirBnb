package com.kaushal.projects.airBnbApp.dto;
import com.kaushal.projects.airBnbApp.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private long id;

    private Integer roomCount;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private BookingStatus bookingStatus;

    private Set<GuestDto> guests;

    private BigDecimal amount;
}
