package com.reliaquest.api.exception.handler;

import com.reliaquest.api.dto.response.ErrorResponseDTO;
import com.reliaquest.api.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Principle Controller responsible for handling all the exceptions.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception exception, HttpServletRequest request){

        log.error("Unhandled exception occurred | path={} | message={}", request.getRequestURI(), exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponseDTO.builder()
                        .errorMessage(exception.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponseDTO> handleRateLimitException(TooManyRequestsException exception, HttpServletRequest request){

        log.error("Application Rate limit exceeded | path={} | message={}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ErrorResponseDTO.builder()
                .errorMessage(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException exception, HttpServletRequest request){

        log.error("Invalid request error | path={} | message={}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDTO.builder()
                .errorMessage(exception.getAllErrors().get(0).getDefaultMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
