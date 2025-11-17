package com.kaushal.projects.airBnbApp.advice;

import com.kaushal.projects.airBnbApp.exceptions.ResourceInUseException;
import com.kaushal.projects.airBnbApp.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> noSuchElementError (ResourceNotFoundException exception)
    {
        ApiError apiError = ApiError.builder().
                status(HttpStatus.NOT_FOUND)
                .message("Resource Not Found")
                .detailedMessage(exception.getMessage()).build();

        return new ResponseEntity<>(new ApiResponse<>(apiError),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ApiResponse<?>> cannotDeleteError (ResourceInUseException exception)
    {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message("Resource is used somewhere as reference.")
                .detailedMessage(exception.getMessage()).build();

        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> inputValidationError(MethodArgumentNotValidException exception)
    {
            List<String> errors = exception
                    .getBindingResult()
                    .getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            ApiError apierror = ApiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .subErrors(errors)
                    .message("Invalid Validation Failed.")
                    .build();
            return new ResponseEntity<>(new ApiResponse<>(apierror),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> authenticationError(AuthenticationException exception)
    {
        ApiError apiError = ApiError
                .builder()
                .message("UnAuthorized User")
                .detailedMessage(exception.getMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> jwtAuthenticationError(JwtException exception)
    {
        ApiError apiError = ApiError
                .builder()
                .message("UnAuthorized Request")
                .detailedMessage(exception.getMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception)
    {
        ApiError apiError = ApiError
                .builder()
                .message("Access Denied")
                .detailedMessage(exception.getMessage())
                .status(HttpStatus.FORBIDDEN)
                .build();

        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.FORBIDDEN);
    }
}
