package com.kaushal.projects.airBnbApp.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class HotelContactInfo {

    private String address;
    private String phoneNumber;

    @Email
    private String email;
    private String location;

}
