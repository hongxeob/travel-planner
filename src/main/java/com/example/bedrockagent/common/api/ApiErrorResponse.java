package com.example.bedrockagent.common.api;

public record ApiErrorResponse(
        String code,
        String message,
        String traceId,
        boolean retryable
) {
}
