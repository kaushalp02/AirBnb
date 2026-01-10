package com.kaushal.projects.airBnbApp.dto;

import com.kaushal.projects.airBnbApp.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;

}
