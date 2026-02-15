package com.example.bedrockagent.config;

import com.example.bedrockagent.travel.service.TravelPlanModelClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnBean(ChatModel.class)
    ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    @ConditionalOnBean(ChatClient.class)
    TravelPlanModelClient bedrockTravelPlanModelClient(ChatClient chatClient) {
        return (systemPrompt, userPrompt, tools) -> {
            var spec = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt);

            if (tools != null && tools.length > 0) {
                spec = spec.tools(tools);
            }

            var content = spec.call().content();
            return content == null ? "" : content;
        };
    }

    @Bean
    @ConditionalOnMissingBean(TravelPlanModelClient.class)
    TravelPlanModelClient fallbackTravelPlanModelClient() {
        return (systemPrompt, userPrompt, tools) -> """
                {
                  "summary": "Travel planning result unavailable",
                  "itinerary": [],
                  "budget": "",
                  "weatherAlerts": [],
                  "assumptions": ["AI model is not configured"],
                  "traceId": ""
                }
                """;
    }
}
