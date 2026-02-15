package com.example.bedrockagent.travel.api;

import java.util.List;

public record TravelPlanResponse(
        String summary,
        List<String> itinerary,
        String budget,
        List<String> weatherAlerts,
        List<String> assumptions,
        String traceId
) {
}
