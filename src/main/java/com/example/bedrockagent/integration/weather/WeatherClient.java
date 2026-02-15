package com.example.bedrockagent.integration.weather;

public interface WeatherClient {

    WeatherResult forecast(double lat, double lon, int days);
}
