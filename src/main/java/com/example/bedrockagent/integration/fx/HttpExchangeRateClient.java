package com.example.bedrockagent.integration.fx;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HttpExchangeRateClient implements ExchangeRateClient {

    private final WebClient webClient;

    public HttpExchangeRateClient(@Qualifier("fxWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ExchangeRateResult rate(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fx/rate")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateResult.class)
                .retry(1)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Empty exchange rate response"));
    }
}
