package com.kaushal.projects.airBnbApp.security;

import com.fasterxml.jackson.core.ObjectCodec;
import com.kaushal.projects.airBnbApp.advice.ApiError;
import com.kaushal.projects.airBnbApp.advice.ApiResponse;
import com.kaushal.projects.airBnbApp.advice.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.nio.file.AccessDeniedException;

import static com.kaushal.projects.airBnbApp.entity.enums.Role.HOTEL_MANAGER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private static final String[] PUBLIC_UNSECURED_URLS = {
            // --- SWAGGER/OPENAPI ---
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",

            // --- ACTUATOR ---
            "/actuator/**",

            // --- AUTHENTICATION/REGISTRATION Endpoints ---
            // REPLACE THESE with the exact paths of your AuthController methods (e.g., /auth/login)
            "/auth/**",
            "/public/**"
    };


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(PUBLIC_UNSECURED_URLS).permitAll()
                                .requestMatchers("/admin/**").hasRole(HOTEL_MANAGER.name())
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exHandlingConfig -> exHandlingConfig.accessDeniedHandler(customAccessDeniedHandler));
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
