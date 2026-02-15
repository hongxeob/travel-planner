package com.example.bedrockagent.integration.places;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HttpPlacesClient implements PlacesClient {

    private final WebClient webClient;

    public HttpPlacesClient(@Qualifier("placesWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public PlaceResult search(String destination) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/places/search")
                        .queryParam("destination", destination)
                        .build())
                .retrieve()
                .bodyToMono(PlaceResult.class)
                .retry(1)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Empty places response"));
    }
}
