package com.twitchspamdetector.moderationengine.exception;

import com.twitchspamdetector.moderationengine.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        ErrorResponseDto body = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message(fieldError != null ? fieldError.getDefaultMessage() : "Solicitud invalida")
                .field(fieldError != null ? fieldError.getField() : null)
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(CentralApiUnavailableException.class)
    public ResponseEntity<ErrorResponseDto> handleCentralApiUnavailable(CentralApiUnavailableException ex,
                                                                         HttpServletRequest request) {
        ErrorResponseDto body = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_GATEWAY.value())
                .error("CENTRAL_API_UNAVAILABLE")
                .message(ex.getMessage())
                .field(null)
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception ex, HttpServletRequest request) {
        ErrorResponseDto body = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_ERROR")
                .message("Ocurrio un error inesperado")
                .field(null)
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
