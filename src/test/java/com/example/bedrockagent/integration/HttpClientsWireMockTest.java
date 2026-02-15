package com.example.bedrockagent.integration;

import com.example.bedrockagent.integration.places.PlaceResult;
import com.example.bedrockagent.integration.places.PlacesClient;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "integration.places.base-url=http://localhost:${wiremock.server.port}",
        "integration.weather.base-url=http://localhost:${wiremock.server.port}",
        "integration.fx.base-url=http://localhost:${wiremock.server.port}"
})
class HttpClientsWireMockTest {

    @Autowired
    private PlacesClient placesClient;

    @Test
    void mapsPlacesResponse() {
        stubFor(get(urlPathEqualTo("/places/search"))
                .willReturn(okJson("{\"name\":\"Tokyo\",\"lat\":35.68,\"lon\":139.76}")));

        PlaceResult result = placesClient.search("Tokyo");

        assertThat(result.name()).isEqualTo("Tokyo");
        assertThat(result.lat()).isEqualTo(35.68);
        assertThat(result.lon()).isEqualTo(139.76);
    }
}
