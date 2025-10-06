package com.wallet.api.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.wallet.api.dto.ApiError;
import com.wallet.api.dto.ApiFieldError;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    /**
     * Handles validation exceptions thrown when method arguments annotated with @Valid fail validation.
     * Constructs a detailed API error response including field-specific error messages.
     *
     * @param ex The MethodArgumentNotValidException that was thrown.
     * @param request The current HttpServletRequest.
     * @return A ResponseEntity containing an ApiError object with validation details and HTTP status BAD_REQUEST.
     */
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiFieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiFieldError(fe.getField(), resolveMessage(fe)))
                .collect(Collectors.toList());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    /**
     * Handles exceptions of type ResponseStatusException, which are typically thrown
     * to indicate a specific HTTP status code and reason.
     *
     * @param ex The ResponseStatusException that was thrown.
     * @param request The current HttpServletRequest.
     * @return A ResponseEntity containing an ApiError object with the specified status and reason.
     */
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ApiError body = ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                ex.getReason(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    /**
     * Handles all other generic exceptions not specifically caught by other handlers.
     * Returns a generic internal server error response.
     *
     * @param ex The generic Exception that was thrown.
     * @param request The current HttpServletRequest.
     * @return A ResponseEntity containing a generic ApiError object with HTTP status INTERNAL_SERVER_ERROR.
     */
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError body = ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Resolves the error message for a given FieldError.
     * If a default message is available, it is used; otherwise, the FieldError's string representation is returned.
     *
     * @param fe The FieldError to resolve the message for.
     * @return The resolved error message.
     */
    private String resolveMessage(FieldError fe) {
        if (fe.getDefaultMessage() != null) {
            return fe.getDefaultMessage();
        }
        return fe.toString();
    }
}


