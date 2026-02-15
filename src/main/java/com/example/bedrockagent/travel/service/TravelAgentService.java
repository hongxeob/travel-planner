package com.example.bedrockagent.travel.service;

import com.example.bedrockagent.integration.fx.ExchangeRateResult;
import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.weather.WeatherResult;
import com.example.bedrockagent.travel.api.TravelPlanRequest;
import com.example.bedrockagent.travel.api.TravelPlanResponse;
import com.example.bedrockagent.travel.prompt.TravelPrompts;
import com.example.bedrockagent.travel.tool.ExchangeRateTool;
import com.example.bedrockagent.travel.tool.PlaceSearchTool;
import com.example.bedrockagent.travel.tool.WeatherTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TravelAgentService {

    private static final String WEATHER_FALLBACK_ASSUMPTION = "Weather data unavailable. Used destination defaults.";
    private static final String FX_FALLBACK_ASSUMPTION = "Exchange rate unavailable. Budget conversion may be inaccurate.";

    private final PlaceSearchTool placeSearchTool;
    private final WeatherTool weatherTool;
    private final ExchangeRateTool exchangeRateTool;
    private final ObjectMapper objectMapper;
    private final TravelPlanModelClient modelClient;

    public TravelAgentService(
            PlaceSearchTool placeSearchTool,
            WeatherTool weatherTool,
            ExchangeRateTool exchangeRateTool,
            ObjectMapper objectMapper,
            TravelPlanModelClient modelClient
    ) {
        this.placeSearchTool = placeSearchTool;
        this.weatherTool = weatherTool;
        this.exchangeRateTool = exchangeRateTool;
        this.objectMapper = objectMapper;
        this.modelClient = modelClient;
    }

    public TravelPlanResponse plan(TravelPlanRequest request) {
        PlaceResult place = placeSearchTool.apply(request.destination());

        List<String> fallbackAssumptions = new ArrayList<>();

        WeatherResult weather;
        try {
            weather = weatherTool.apply(place.lat(), place.lon(), request.days());
        } catch (Exception ignored) {
            weather = new WeatherResult(List.of());
            fallbackAssumptions.add(WEATHER_FALLBACK_ASSUMPTION);
        }

        ExchangeRateResult exchangeRate;
        try {
            exchangeRate = exchangeRateTool.apply("KRW", "JPY");
        } catch (Exception ignored) {
            exchangeRate = new ExchangeRateResult("KRW", "JPY", BigDecimal.ZERO);
            fallbackAssumptions.add(FX_FALLBACK_ASSUMPTION);
        }

        String modelRaw = modelClient.generate(
                TravelPrompts.systemPrompt(),
                TravelPrompts.userPrompt(request, place, weather, exchangeRate),
                placeSearchTool,
                weatherTool,
                exchangeRateTool
        );

        TravelPlanResponse parsed = parseModelResponse(modelRaw);
        List<String> assumptions = new ArrayList<>(safeList(parsed.assumptions()));
        assumptions.addAll(fallbackAssumptions);

        return new TravelPlanResponse(
                parsed.summary(),
                safeList(parsed.itinerary()),
                parsed.budget(),
                safeList(parsed.weatherAlerts()),
                assumptions,
                parsed.traceId()
        );
    }

    private TravelPlanResponse parseModelResponse(String content) {
        try {
            return objectMapper.readValue(content, TravelPlanResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid model response", e);
        }
    }

    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    public TravelPlanResponse fallbackResponse(String traceId) {
        return new TravelPlanResponse(
                "Travel planning result unavailable",
                List.of(),
                "",
                List.of(),
                List.of("AI model is not configured"),
                traceId
        );
    }
}
