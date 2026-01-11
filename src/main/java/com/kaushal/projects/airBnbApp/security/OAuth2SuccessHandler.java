package com.kaushal.projects.airBnbApp.security;

import com.kaushal.projects.airBnbApp.dto.LoginResponseDto;
import com.kaushal.projects.airBnbApp.entity.User;
import com.kaushal.projects.airBnbApp.entity.enums.Role;
import com.kaushal.projects.airBnbApp.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElse(null);

        //Create new user if does not exists already
        if (user == null)
        {
            //login the user
            User newUser = User.builder()
                    .name(oAuth2User.getAttribute("name"))
                    .email(email)
                    .password("OauthUserNoPassword")
                    .roles(Set.of(Role.GUEST))
                    .build();

            user = userRepository.save(newUser);
            log.info("New user created with google Oauth with email : {}", email);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        String targetUrl = "http://localhost:8080/api/v1/auth/token?accessToken=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
