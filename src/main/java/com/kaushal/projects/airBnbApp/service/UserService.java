package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.UserDto;
import com.kaushal.projects.airBnbApp.dto.UserRoleUpdateDto;
import com.kaushal.projects.airBnbApp.dto.UserUpdateDto;
import com.kaushal.projects.airBnbApp.entity.User;
import jakarta.validation.Valid;

public interface UserService {

    public User getUserById(Long id);

    void updateUser(UserUpdateDto userUpdateDto);

    UserDto getUserInfo();

    void updateUserRole(Long userId, UserRoleUpdateDto roleUpdateDto);
}
