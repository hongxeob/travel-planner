package com.example.bedrockagent.travel.tool;

import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.places.PlacesClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TravelToolsTest {

    @Test
    void placeToolCallsPlacesClient() {
        var places = mock(PlacesClient.class);
        when(places.search("Tokyo")).thenReturn(new PlaceResult("Tokyo", 35.68, 139.76));

        var tool = new PlaceSearchTool(places);
        var result = tool.apply("Tokyo");

        assertThat(result.name()).isEqualTo("Tokyo");
    }
}
