package com.example.bedrockagent.travel.prompt;

import com.example.bedrockagent.integration.fx.ExchangeRateResult;
import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.weather.WeatherResult;
import com.example.bedrockagent.travel.api.TravelPlanRequest;

public final class TravelPrompts {

    private TravelPrompts() {
    }

    public static String systemPrompt() {
        return """
                You are a travel planning agent.
                Return a strict JSON object with keys:
                summary, itinerary, budget, weatherAlerts, assumptions, traceId.
                itinerary, weatherAlerts, assumptions must be arrays of strings.
                Do not include markdown fences.
                """;
    }

    public static String userPrompt(
            TravelPlanRequest request,
            PlaceResult place,
            WeatherResult weather,
            ExchangeRateResult exchangeRate
    ) {
        return """
                origin: %s
                destination: %s
                days: %d
                budget_krw: %s
                preferences: %s
                place_name: %s
                place_lat: %s
                place_lon: %s
                weather_daily_summaries: %s
                exchange_rate: %s %s->%s
                """.formatted(
                request.origin(),
                request.destination(),
                request.days(),
                request.budgetKrw(),
                request.preferences(),
                place.name(),
                place.lat(),
                place.lon(),
                weather.dailySummaries(),
                exchangeRate.rate(),
                exchangeRate.from(),
                exchangeRate.to()
        );
    }
}
