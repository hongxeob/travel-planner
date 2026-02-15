package com.example.bedrockagent.travel.api;

import com.example.bedrockagent.travel.service.TravelAgentService;
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

    @PostMapping("/plan")
    public TravelPlanResponse plan(@Valid @RequestBody TravelPlanRequest request) {
        return service.plan(request);
    }
}
