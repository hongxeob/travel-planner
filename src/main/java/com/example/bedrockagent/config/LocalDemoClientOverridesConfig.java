package com.example.bedrockagent.config;

import com.example.bedrockagent.integration.fx.ExchangeRateClient;
import com.example.bedrockagent.integration.fx.ExchangeRateResult;
import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.places.PlacesClient;
import com.example.bedrockagent.integration.weather.WeatherClient;
import com.example.bedrockagent.integration.weather.WeatherResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Profile("local")
public class LocalDemoClientOverridesConfig {

    @Bean
    @Primary
    PlacesClient localPlacesClient() {
        return destination -> new PlaceResult(destination, 35.68, 139.76);
    }

    @Bean
    @Primary
    WeatherClient localWeatherClient() {
        return (lat, lon, days) -> new WeatherResult(List.of("Sunny"));
    }

    @Bean
    @Primary
    ExchangeRateClient localExchangeRateClient() {
        return (from, to) -> new ExchangeRateResult(from, to, new BigDecimal("0.11"));
    }
}
