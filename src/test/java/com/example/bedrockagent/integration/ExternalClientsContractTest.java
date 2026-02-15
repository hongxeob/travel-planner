package com.example.bedrockagent.integration;

import com.example.bedrockagent.integration.fx.ExchangeRateClient;
import com.example.bedrockagent.integration.places.PlacesClient;
import com.example.bedrockagent.integration.weather.WeatherClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalClientsContractTest {

    @Test
    void interfacesExposeMinimumMethods() {
        assertThat(PlacesClient.class.getDeclaredMethods()).isNotEmpty();
        assertThat(WeatherClient.class.getDeclaredMethods()).isNotEmpty();
        assertThat(ExchangeRateClient.class.getDeclaredMethods()).isNotEmpty();
    }
}
