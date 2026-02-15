package com.example.bedrockagent.travel.tool;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.annotation.Tool;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class TravelToolsAnnotationTest {

    @Test
    void allToolMethodsUseToolAnnotation() throws Exception {
        assertToolAnnotated(PlaceSearchTool.class, "apply", String.class);
        assertToolAnnotated(WeatherTool.class, "apply", double.class, double.class, int.class);
        assertToolAnnotated(ExchangeRateTool.class, "apply", String.class, String.class);
    }

    private void assertToolAnnotated(Class<?> type, String methodName, Class<?>... parameterTypes) throws Exception {
        Method method = type.getMethod(methodName, parameterTypes);
        assertThat(method.isAnnotationPresent(Tool.class)).isTrue();
    }
}
