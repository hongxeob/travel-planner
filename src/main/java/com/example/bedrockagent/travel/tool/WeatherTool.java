package com.example.bedrockagent.travel.tool;

import com.example.bedrockagent.integration.weather.WeatherClient;
import com.example.bedrockagent.integration.weather.WeatherResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class WeatherTool {

    private final WeatherClient weatherClient;

    public WeatherTool(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @Tool(description = "Fetches weather forecast summaries for coordinates and trip length")
    public WeatherResult apply(double lat, double lon, int days) {
        return weatherClient.forecast(lat, lon, days);
    }
}
