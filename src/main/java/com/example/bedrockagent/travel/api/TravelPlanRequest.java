package com.example.bedrockagent.travel.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TravelPlanRequest(
        @NotBlank String origin,
        @NotBlank String destination,
        @Min(1) int days,
        @NotNull BigDecimal budgetKrw,
        String preferences
) {
}
