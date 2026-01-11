package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.dto.UserDto;
import com.kaushal.projects.airBnbApp.dto.UserRoleUpdateDto;
import com.kaushal.projects.airBnbApp.dto.UserUpdateDto;
import com.kaushal.projects.airBnbApp.entity.User;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.exceptions.UnAuthorizedException;
import com.kaushal.projects.airBnbApp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.kaushal.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService
{
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateDto userUpdateDto) {

        log.info("Updating the user information");
        User user = getCurrentUser();

        if (userUpdateDto.getDateOfBirth() != null) user.setDateOfBirth(userUpdateDto.getDateOfBirth());
        if (userUpdateDto.getName() != null ) user.setName(userUpdateDto.getName());
        if (userUpdateDto.getGender() != null) user.setGender(userUpdateDto.getGender());

        userRepository.save(user);
    }

    @Override
    public UserDto getUserInfo() {
        return modelMapper.map(getCurrentUser(), UserDto.class);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, UserRoleUpdateDto roleUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+userId));
        User loggedInUser = getCurrentUser();

        if (!user.equals(loggedInUser))
            throw new UnAuthorizedException("You are not authorized to change other users roles");

        user.setRoles(roleUpdateDto.getRoles());

        userRepository.save(user);
    }
}
