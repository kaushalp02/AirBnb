package com.kaushal.projects.airBnbApp.dto;

import com.kaushal.projects.airBnbApp.entity.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    @Size(min = 3, max = 20, message = "Invalid length for name. Allowed 3 to 20 Characters.")
    @NotBlank(message = "Name is mandatory and cannot be blank")
    private String name;

    @Email
    @NotBlank(message = "Email is mandatory and cannot be blank")
    private String email;

    @Size(min = 8, message = "Password should be atleast 8 character long.")
    @NotBlank(message = "Password is mandatory and cannot be blank")
    private String password;

    @Past
    private LocalDate DateOfBirth;

    @NotNull(message = "gender is mandatory and cannot be blank")
    private Gender gender;
}
