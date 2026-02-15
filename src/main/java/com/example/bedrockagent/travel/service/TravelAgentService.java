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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TravelAgentService {

    private static final Logger log = LoggerFactory.getLogger(TravelAgentService.class);

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
        long startedAt = System.currentTimeMillis();

        PlaceResult place = callTool("placeSearch", () -> placeSearchTool.apply(request.destination()));

        List<String> fallbackAssumptions = new ArrayList<>();

        WeatherResult weather;
        try {
            weather = callTool("weather", () -> weatherTool.apply(place.lat(), place.lon(), request.days()));
        } catch (Exception ex) {
            log.warn("TOOL_CALL_FAIL tool=weather reason={}", ex.getMessage());
            weather = new WeatherResult(List.of());
            fallbackAssumptions.add(WEATHER_FALLBACK_ASSUMPTION);
        }

        ExchangeRateResult exchangeRate;
        try {
            exchangeRate = callTool("exchangeRate", () -> exchangeRateTool.apply("KRW", "JPY"));
        } catch (Exception ex) {
            log.warn("TOOL_CALL_FAIL tool=exchangeRate reason={}", ex.getMessage());
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

        TravelPlanResponse response = new TravelPlanResponse(
                parsed.summary(),
                safeList(parsed.itinerary()),
                parsed.budget(),
                safeList(parsed.weatherAlerts()),
                assumptions,
                parsed.traceId()
        );

        log.info("TRAVEL_PLAN_DONE durationMs={}", System.currentTimeMillis() - startedAt);
        return response;
    }

    private TravelPlanResponse parseModelResponse(String content) {
        try {
            return objectMapper.readValue(content, TravelPlanResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid model response", e);
        }
    }

    private <T> T callTool(String toolName, ToolSupplier<T> supplier) {
        long startedAt = System.currentTimeMillis();
        log.info("TOOL_CALL_START tool={}", toolName);
        try {
            T result = supplier.get();
            log.info("TOOL_CALL_END tool={} durationMs={} status=success", toolName, System.currentTimeMillis() - startedAt);
            return result;
        } catch (RuntimeException ex) {
            log.info("TOOL_CALL_END tool={} durationMs={} status=failed", toolName, System.currentTimeMillis() - startedAt);
            throw ex;
        }
    }

    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    @FunctionalInterface
    private interface ToolSupplier<T> {
        T get();
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
