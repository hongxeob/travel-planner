package com.example.bedrockagent.travel.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelPlanOpenApiTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void swaggerEndpointIsAvailable() {
        var body = rest.getForObject("/v3/api-docs", String.class);
        assertThat(body).contains("/api/v1/travel/plan");
    }
}
