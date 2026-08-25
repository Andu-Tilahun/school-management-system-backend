package com.schoolmanagment.commonapplication.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;
    private List validationErrors;
    private String correlationId;
    private LocalDateTime timestamp;

    // Success response with data
    public ApiResponse success(T data, String message) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Success response without data
    public static ApiResponse success(String message) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error response
    public static ApiResponse error(String message, ErrorDetails error) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Validation error response
    public static ApiResponse validationError(String message, List errors) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .validationErrors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
