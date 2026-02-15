package com.example.bedrockagent.integration.weather;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HttpWeatherClient implements WeatherClient {

    private final WebClient webClient;

    public HttpWeatherClient(@Qualifier("weatherWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public WeatherResult forecast(double lat, double lon, int days) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/weather/forecast")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("days", days)
                        .build())
                .retrieve()
                .bodyToMono(WeatherResult.class)
                .retry(1)
                .blockOptional()
                .orElseGet(() -> new WeatherResult(java.util.List.of()));
    }
}
