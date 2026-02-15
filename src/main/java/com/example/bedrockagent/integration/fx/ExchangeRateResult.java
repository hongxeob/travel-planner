package com.example.bedrockagent.integration.fx;

import java.math.BigDecimal;

public record ExchangeRateResult(
        String from,
        String to,
        BigDecimal rate
) {
}
