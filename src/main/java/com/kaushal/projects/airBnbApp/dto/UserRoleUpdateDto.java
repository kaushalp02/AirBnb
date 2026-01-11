package com.kaushal.projects.airBnbApp.dto;

import com.kaushal.projects.airBnbApp.entity.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UserRoleUpdateDto {
    @NotEmpty(message = "At least one role must be assigned")
    private Set<Role> roles;
}
