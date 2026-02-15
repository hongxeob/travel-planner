package com.example.bedrockagent.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({
        WebClientConfig.PlacesProperties.class,
        WebClientConfig.WeatherProperties.class,
        WebClientConfig.FxProperties.class
})
public class WebClientConfig {

    @Bean
    @Qualifier("placesWebClient")
    WebClient placesWebClient(PlacesProperties properties) {
        return baseClient(properties.baseUrl());
    }

    @Bean
    @Qualifier("weatherWebClient")
    WebClient weatherWebClient(WeatherProperties properties) {
        return baseClient(properties.baseUrl());
    }

    @Bean
    @Qualifier("fxWebClient")
    WebClient fxWebClient(FxProperties properties) {
        return baseClient(properties.baseUrl());
    }

    private WebClient baseClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @ConfigurationProperties("integration.places")
    public record PlacesProperties(String baseUrl) {}

    @ConfigurationProperties("integration.weather")
    public record WeatherProperties(String baseUrl) {}

    @ConfigurationProperties("integration.fx")
    public record FxProperties(String baseUrl) {}
}
