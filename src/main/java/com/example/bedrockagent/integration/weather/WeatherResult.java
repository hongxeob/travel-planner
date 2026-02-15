package com.example.bedrockagent.integration.weather;

import java.util.List;

public record WeatherResult(
        List<String> dailySummaries
) {
}
