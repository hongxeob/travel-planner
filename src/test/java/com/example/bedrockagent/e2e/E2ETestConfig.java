package com.example.bedrockagent.e2e;

import com.example.bedrockagent.integration.fx.ExchangeRateClient;
import com.example.bedrockagent.integration.fx.ExchangeRateResult;
import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.places.PlacesClient;
import com.example.bedrockagent.integration.weather.WeatherClient;
import com.example.bedrockagent.integration.weather.WeatherResult;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.List;

@TestConfiguration
public class E2ETestConfig {

    @Bean
    @Primary
    PlacesClient placesClient() {
        return destination -> new PlaceResult(destination, 35.68, 139.76);
    }

    @Bean
    @Primary
    WeatherClient weatherClient() {
        return (lat, lon, days) -> new WeatherResult(List.of("Sunny"));
    }

    @Bean
    @Primary
    ExchangeRateClient exchangeRateClient() {
        return (from, to) -> new ExchangeRateResult(from, to, new BigDecimal("0.11"));
    }
}
