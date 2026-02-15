package com.example.bedrockagent.travel.tool;

import com.example.bedrockagent.integration.weather.WeatherClient;
import com.example.bedrockagent.integration.weather.WeatherResult;
import org.springframework.stereotype.Component;

@Component
public class WeatherTool {

    private final WeatherClient weatherClient;

    public WeatherTool(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public WeatherResult apply(double lat, double lon, int days) {
        return weatherClient.forecast(lat, lon, days);
    }
}
