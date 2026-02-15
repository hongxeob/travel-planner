package com.example.bedrockagent.travel.service;

import com.example.bedrockagent.travel.api.TravelPlanRequest;
import com.example.bedrockagent.travel.api.TravelPlanResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelAgentService {

    public TravelPlanResponse plan(TravelPlanRequest request) {
        return new TravelPlanResponse("", List.of(), "", List.of(), List.of(), "");
    }
}
