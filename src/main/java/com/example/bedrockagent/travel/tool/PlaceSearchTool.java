package com.example.bedrockagent.travel.tool;

import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.places.PlacesClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class PlaceSearchTool {

    private final PlacesClient placesClient;

    public PlaceSearchTool(PlacesClient placesClient) {
        this.placesClient = placesClient;
    }

    @Tool(description = "Searches destination place information including coordinates")
    public PlaceResult apply(String destination) {
        return placesClient.search(destination);
    }
}
