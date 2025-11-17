package com.kaushal.projects.airBnbApp.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) {

        try {
            // Writing custom error response to match our generic error response
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            //Creating api error structure
            ApiError apiError = ApiError.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .message("Access Denied")
                    .detailedMessage(accessDeniedException.getMessage())
                    .build();

            // creating api response structure
            ApiResponse<?> wrappedResponse = new ApiResponse<>(apiError);

            objectMapper.writeValue(response.getOutputStream(), wrappedResponse);

        } catch (Exception e) {
            log.error("Error while creating response of forbidden access exception.");
        }
    }
}