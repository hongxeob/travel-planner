package com.example.bedrockagent.integration.fx;

public interface ExchangeRateClient {

    ExchangeRateResult rate(String from, String to);
}
