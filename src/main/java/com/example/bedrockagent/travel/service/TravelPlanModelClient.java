package com.example.bedrockagent.travel.service;

@FunctionalInterface
public interface TravelPlanModelClient {

    String generate(String systemPrompt, String userPrompt, Object... tools);
}
