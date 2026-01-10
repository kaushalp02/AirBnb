package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.UserDto;
import com.kaushal.projects.airBnbApp.dto.UserUpdateDto;
import com.kaushal.projects.airBnbApp.entity.User;

public interface UserService {

    public User getUserById(Long id);

    void updateUser(UserUpdateDto userUpdateDto);

    UserDto getUserInfo();
}
