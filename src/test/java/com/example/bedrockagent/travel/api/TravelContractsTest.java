package com.example.bedrockagent.travel.api;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TravelContractsTest {

    @Test
    void requestValidationRequiresRequiredFields() {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        var req = new TravelPlanRequest(null, "", 0, null, null);
        var violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
    }
}
