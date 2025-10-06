package com.wallet.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<ApiFieldError> errors
) {
    public static ApiError of(int status, String error, String message, String path, List<ApiFieldError> errors) {
        return new ApiError(OffsetDateTime.now(), status, error, message, path, errors);
    }
}


