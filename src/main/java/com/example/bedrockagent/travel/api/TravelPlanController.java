package com.example.bedrockagent.travel.api;

import com.example.bedrockagent.travel.service.TravelAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/travel")
public class TravelPlanController {

    private final TravelAgentService service;

    public TravelPlanController(TravelAgentService service) {
        this.service = service;
    }

    @Operation(
            summary = "Generate travel plan with tool-calling agent",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Travel plan generated",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "summary": "Tokyo 4-day plan",
                                              "itinerary": ["Day 1: Shibuya"],
                                              "budget": "1500000 KRW",
                                              "weatherAlerts": ["Carry a light jacket"],
                                              "assumptions": [],
                                              "traceId": ""
                                            }
                                            """)
                            )
                    )
            }
    )
    @PostMapping("/plan")
    public TravelPlanResponse plan(@Valid @RequestBody TravelPlanRequest request) {
        return service.plan(request);
    }
}
