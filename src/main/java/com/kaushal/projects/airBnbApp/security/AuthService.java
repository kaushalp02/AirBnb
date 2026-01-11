package com.kaushal.projects.airBnbApp.security;

import com.kaushal.projects.airBnbApp.dto.LoginRequestDto;
import com.kaushal.projects.airBnbApp.dto.SignUpRequestDto;
import com.kaushal.projects.airBnbApp.dto.UserDto;
import com.kaushal.projects.airBnbApp.entity.User;
import com.kaushal.projects.airBnbApp.entity.enums.Role;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import com.kaushal.projects.airBnbApp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UserDto signUp(SignUpRequestDto signUpRequestDto){

        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);

        if (user != null)
        {
            log.error("user already exists with the same email");
            throw new RuntimeException("User with email already exists : "+signUpRequestDto.getEmail());
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);

        //add guest role by default for the new user
        newUser.setRoles(Set.of(Role.GUEST));

        //Encrypt the password and set it in the new user
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        userRepository.save(newUser);
        log.info("Successfully Created New User");
        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginRequestDto loginRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()
        ));

        User user = (User) authentication.getPrincipal();

        String[] tokens = new String[2];

        tokens[0] = jwtService.generateAccessToken(user);
        tokens[1] = jwtService.generateRefreshToken(user);

        return tokens;
    }

    public String refreshToken(String refreshToken)
    {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("User not found with the id "+id));

        return jwtService.generateAccessToken(user);
    }
}
