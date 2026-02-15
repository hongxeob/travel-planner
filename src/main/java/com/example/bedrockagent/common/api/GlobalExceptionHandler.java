package com.example.bedrockagent.common.api;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        var firstError = ex.getBindingResult().getAllErrors().stream().findFirst();
        var message = firstError
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + " " + error.getDefaultMessage()
                        : Optional.ofNullable(error.getDefaultMessage()).orElse("Request validation failed"))
                .orElse("Request validation failed");

        return ResponseEntity.badRequest().body(
                new ApiErrorResponse("VALIDATION_ERROR", message, resolveTraceId(request), false)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.internalServerError().body(
                new ApiErrorResponse("INTERNAL_ERROR", "Unexpected server error", resolveTraceId(request), true)
        );
    }

    private String resolveTraceId(HttpServletRequest request) {
        var traceId = request.getAttribute("traceId");
        if (traceId instanceof String value && !value.isBlank()) {
            return value;
        }
        var mdcTraceId = MDC.get("traceId");
        return mdcTraceId == null ? "" : mdcTraceId;
    }
}
