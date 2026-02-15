package com.example.bedrockagent.e2e;

import com.example.bedrockagent.travel.api.TravelPlanRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(E2ETestConfig.class)
class TravelPlanE2ETest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void happyPathResponseContainsRequiredFields() {
        var request = new TravelPlanRequest(
                "Seoul",
                "Tokyo",
                4,
                new BigDecimal("1500000"),
                "food and shopping"
        );

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = rest.postForEntity(
                "/api/v1/travel/plan",
                new HttpEntity<>(request, headers),
                Map.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsKeys("summary", "itinerary", "budget", "assumptions");
    }
}
