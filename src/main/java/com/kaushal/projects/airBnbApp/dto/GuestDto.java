package com.kaushal.projects.airBnbApp.dto;

import com.kaushal.projects.airBnbApp.entity.User;
import com.kaushal.projects.airBnbApp.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {

    private long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
