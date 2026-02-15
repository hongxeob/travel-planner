package com.example.bedrockagent.travel.tool;

import com.example.bedrockagent.integration.fx.ExchangeRateClient;
import com.example.bedrockagent.integration.fx.ExchangeRateResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateTool {

    private final ExchangeRateClient exchangeRateClient;

    public ExchangeRateTool(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    @Tool(description = "Returns exchange rate between source and target currency")
    public ExchangeRateResult apply(String from, String to) {
        return exchangeRateClient.rate(from, to);
    }
}
