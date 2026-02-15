package com.example.bedrockagent.travel.service;

import com.example.bedrockagent.integration.fx.ExchangeRateResult;
import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.weather.WeatherResult;
import com.example.bedrockagent.travel.api.TravelPlanRequest;
import com.example.bedrockagent.travel.tool.ExchangeRateTool;
import com.example.bedrockagent.travel.tool.PlaceSearchTool;
import com.example.bedrockagent.travel.tool.WeatherTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TravelAgentServiceTest {

    @Test
    void returnsStructuredResponseShape() {
        var placeTool = mock(PlaceSearchTool.class);
        when(placeTool.apply("Tokyo")).thenReturn(new PlaceResult("Tokyo", 35.68, 139.76));

        var weatherTool = mock(WeatherTool.class);
        when(weatherTool.apply(35.68, 139.76, 4)).thenReturn(new WeatherResult(List.of("Sunny")));

        var exchangeRateTool = mock(ExchangeRateTool.class);
        when(exchangeRateTool.apply("KRW", "JPY"))
                .thenReturn(new ExchangeRateResult("KRW", "JPY", new BigDecimal("0.11")));

        var service = new TravelAgentService(
                placeTool,
                weatherTool,
                exchangeRateTool,
                new ObjectMapper(),
                (systemPrompt, userPrompt, tools) -> """
                        {
                          "summary": "Tokyo 4-day plan",
                          "itinerary": ["Day 1: Shibuya"],
                          "budget": "1500000 KRW",
                          "weatherAlerts": ["Carry a light jacket"],
                          "assumptions": [],
                          "traceId": ""
                        }
                        """
        );

        var response = service.plan(new TravelPlanRequest(
                "Seoul",
                "Tokyo",
                4,
                new BigDecimal("1500000"),
                "food"
        ));

        assertThat(response.summary()).isNotBlank();
    }
}
