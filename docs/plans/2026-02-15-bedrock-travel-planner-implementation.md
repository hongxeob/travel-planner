# Bedrock Travel Planner Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a new Spring Boot demo service that uses Spring AI + AWS Bedrock tool calling to orchestrate Places, Weather, and Exchange Rate APIs and return a travel plan via REST + Swagger.

**Architecture:** Start with a single Spring Boot app in a new repo, implement tests first (TDD), then add minimal production code per failing test. Use Spring AI ChatClient with tool beans for orchestration, WebClient-based API clients for external calls, and a thin controller-service split. Keep implementation small, explicit, and demo-focused.

**Tech Stack:** Java 21, Spring Boot 3.x, Spring AI, AWS Bedrock, Spring Web, Validation, Actuator, springdoc-openapi, JUnit 5, WireMock, Mockito, Gradle Kotlin DSL.

---

### Task 1: Bootstrap project skeleton

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `src/main/java/com/example/bedrockagent/BedrockAgentDemoApplication.java`
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/application-local.yml`
- Create: `src/test/java/com/example/bedrockagent/BedrockAgentDemoApplicationTests.java`

**Step 1: Write the failing test**

```java
package com.example.bedrockagent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BedrockAgentDemoApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*BedrockAgentDemoApplicationTests"`
Expected: FAIL with missing Spring Boot setup

**Step 3: Write minimal implementation**

```java
package com.example.bedrockagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BedrockAgentDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BedrockAgentDemoApplication.class, args);
    }
}
```

Plus minimal Gradle/Spring config in the listed files.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*BedrockAgentDemoApplicationTests"`
Expected: PASS

**Step 5: Commit**

```bash
git add settings.gradle.kts build.gradle.kts src/main src/test
git commit -m "chore: bootstrap spring boot demo project"
```

---

### Task 2: Define travel planning request/response contracts

**Files:**
- Create: `src/main/java/com/example/bedrockagent/travel/api/TravelPlanRequest.java`
- Create: `src/main/java/com/example/bedrockagent/travel/api/TravelPlanResponse.java`
- Create: `src/main/java/com/example/bedrockagent/common/api/ApiErrorResponse.java`
- Test: `src/test/java/com/example/bedrockagent/travel/api/TravelContractsTest.java`

**Step 1: Write the failing test**

```java
package com.example.bedrockagent.travel.api;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TravelContractsTest {

    @Test
    void request_validation_requires_required_fields() {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        var req = new TravelPlanRequest(null, "", 0, null, null);
        var violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TravelContractsTest"`
Expected: FAIL because DTOs do not exist

**Step 3: Write minimal implementation**

```java
public record TravelPlanRequest(
    @NotBlank String origin,
    @NotBlank String destination,
    @Min(1) int days,
    @NotNull BigDecimal budgetKrw,
    String preferences
) {}
```

```java
public record TravelPlanResponse(
    String summary,
    List<String> itinerary,
    String budget,
    List<String> weatherAlerts,
    List<String> assumptions,
    String traceId
) {}
```

```java
public record ApiErrorResponse(
    String code,
    String message,
    String traceId,
    boolean retryable
) {}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TravelContractsTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/travel/api src/main/java/com/example/bedrockagent/common/api src/test/java/com/example/bedrockagent/travel/api
git commit -m "feat: add travel request response contracts"
```

---

### Task 3: Add controller endpoint with validation and OpenAPI docs

**Files:**
- Create: `src/main/java/com/example/bedrockagent/travel/api/TravelPlanController.java`
- Create: `src/main/java/com/example/bedrockagent/travel/service/TravelAgentService.java`
- Test: `src/test/java/com/example/bedrockagent/travel/api/TravelPlanControllerTest.java`

**Step 1: Write the failing test**

```java
@WebMvcTest(TravelPlanController.class)
class TravelPlanControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean TravelAgentService service;

    @Test
    void returns_400_on_invalid_request() throws Exception {
        mockMvc.perform(post("/api/v1/travel/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TravelPlanControllerTest"`
Expected: FAIL because controller/service do not exist

**Step 3: Write minimal implementation**

```java
@RestController
@RequestMapping("/api/v1/travel")
class TravelPlanController {

    private final TravelAgentService service;

    @PostMapping("/plan")
    TravelPlanResponse plan(@Valid @RequestBody TravelPlanRequest request) {
        return service.plan(request);
    }
}
```

```java
@Service
public class TravelAgentService {
    public TravelPlanResponse plan(TravelPlanRequest request) {
        return new TravelPlanResponse("", List.of(), "", List.of(), List.of(), "");
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TravelPlanControllerTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/travel src/test/java/com/example/bedrockagent/travel/api
git commit -m "feat: expose travel plan REST endpoint"
```

---

### Task 4: Add standardized exception handling with traceId

**Files:**
- Create: `src/main/java/com/example/bedrockagent/common/api/GlobalExceptionHandler.java`
- Create: `src/main/java/com/example/bedrockagent/common/trace/TraceIdFilter.java`
- Test: `src/test/java/com/example/bedrockagent/common/api/GlobalExceptionHandlerTest.java`

**Step 1: Write the failing test**

```java
@WebMvcTest(TravelPlanController.class)
@Import({GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;
    @MockBean TravelAgentService service;

    @Test
    void maps_validation_error_to_standard_payload() throws Exception {
        mockMvc.perform(post("/api/v1/travel/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*GlobalExceptionHandlerTest"`
Expected: FAIL because handler not present

**Step 3: Write minimal implementation**

Add `@RestControllerAdvice` with handlers for `MethodArgumentNotValidException` and generic `Exception`, returning `ApiErrorResponse` and reading `traceId` from MDC/request attribute.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*GlobalExceptionHandlerTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/common src/test/java/com/example/bedrockagent/common/api
git commit -m "feat: add standard error response and trace id handling"
```

---

### Task 5: Implement external API client contracts and models

**Files:**
- Create: `src/main/java/com/example/bedrockagent/integration/places/PlacesClient.java`
- Create: `src/main/java/com/example/bedrockagent/integration/weather/WeatherClient.java`
- Create: `src/main/java/com/example/bedrockagent/integration/fx/ExchangeRateClient.java`
- Create: `src/main/java/com/example/bedrockagent/integration/*/*Models.java`
- Test: `src/test/java/com/example/bedrockagent/integration/ExternalClientsContractTest.java`

**Step 1: Write the failing test**

```java
class ExternalClientsContractTest {

    @Test
    void interfaces_expose_minimum_methods() {
        assertThat(PlacesClient.class.getDeclaredMethods()).isNotEmpty();
        assertThat(WeatherClient.class.getDeclaredMethods()).isNotEmpty();
        assertThat(ExchangeRateClient.class.getDeclaredMethods()).isNotEmpty();
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*ExternalClientsContractTest"`
Expected: FAIL because interfaces do not exist

**Step 3: Write minimal implementation**

Create interfaces:

```java
public interface PlacesClient {
    PlaceResult search(String destination);
}

public interface WeatherClient {
    WeatherResult forecast(double lat, double lon, int days);
}

public interface ExchangeRateClient {
    ExchangeRateResult rate(String from, String to);
}
```

Add minimal model records used by each client.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*ExternalClientsContractTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/integration src/test/java/com/example/bedrockagent/integration
git commit -m "feat: define external api client contracts"
```

---

### Task 6: Implement WebClient adapters with timeout/retry

**Files:**
- Create: `src/main/java/com/example/bedrockagent/integration/places/HttpPlacesClient.java`
- Create: `src/main/java/com/example/bedrockagent/integration/weather/HttpWeatherClient.java`
- Create: `src/main/java/com/example/bedrockagent/integration/fx/HttpExchangeRateClient.java`
- Create: `src/main/java/com/example/bedrockagent/config/WebClientConfig.java`
- Test: `src/test/java/com/example/bedrockagent/integration/HttpClientsWireMockTest.java`

**Step 1: Write the failing test**

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class HttpClientsWireMockTest {

    @Autowired PlacesClient placesClient;

    @Test
    void maps_places_response() {
        stubFor(get(urlPathEqualTo("/places/search")).willReturn(okJson("{\"name\":\"Tokyo\",\"lat\":35.68,\"lon\":139.76}")));
        var result = placesClient.search("Tokyo");
        assertThat(result.name()).isEqualTo("Tokyo");
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*HttpClientsWireMockTest"`
Expected: FAIL because adapters/config do not exist

**Step 3: Write minimal implementation**

Implement each HTTP client with `WebClient`, base URL from `application-local.yml`, and minimal retry/timeout.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*HttpClientsWireMockTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/config src/main/java/com/example/bedrockagent/integration src/test/java/com/example/bedrockagent/integration
git commit -m "feat: implement webclient adapters for external apis"
```

---

### Task 7: Create Spring AI tool beans for places/weather/fx

**Files:**
- Create: `src/main/java/com/example/bedrockagent/travel/tool/PlaceSearchTool.java`
- Create: `src/main/java/com/example/bedrockagent/travel/tool/WeatherTool.java`
- Create: `src/main/java/com/example/bedrockagent/travel/tool/ExchangeRateTool.java`
- Test: `src/test/java/com/example/bedrockagent/travel/tool/TravelToolsTest.java`

**Step 1: Write the failing test**

```java
class TravelToolsTest {

    @Test
    void place_tool_calls_places_client() {
        var places = mock(PlacesClient.class);
        when(places.search("Tokyo")).thenReturn(new PlaceResult("Tokyo", 35.68, 139.76));
        var tool = new PlaceSearchTool(places);
        var result = tool.apply("Tokyo");
        assertThat(result.name()).isEqualTo("Tokyo");
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TravelToolsTest"`
Expected: FAIL because tools do not exist

**Step 3: Write minimal implementation**

Implement each tool as a small class with a public function method consumable by Spring AI tool calling.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TravelToolsTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/travel/tool src/test/java/com/example/bedrockagent/travel/tool
git commit -m "feat: add spring ai tools for place weather and fx"
```

---

### Task 8: Implement Bedrock-backed agent orchestration

**Files:**
- Modify: `src/main/java/com/example/bedrockagent/travel/service/TravelAgentService.java`
- Create: `src/main/java/com/example/bedrockagent/config/AiConfig.java`
- Create: `src/main/java/com/example/bedrockagent/travel/prompt/TravelPrompts.java`
- Test: `src/test/java/com/example/bedrockagent/travel/service/TravelAgentServiceTest.java`

**Step 1: Write the failing test**

```java
class TravelAgentServiceTest {

    @Test
    void returns_structured_response_shape() {
        var service = new TravelAgentService(/* mocked chat client + tools */);
        var response = service.plan(new TravelPlanRequest("Seoul", "Tokyo", 4, new BigDecimal("1500000"), "food"));
        assertThat(response.summary()).isNotBlank();
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TravelAgentServiceTest"`
Expected: FAIL because service is stubbed

**Step 3: Write minimal implementation**

- Build `ChatClient` call with system prompt constraints.
- Register tools (`PlaceSearchTool`, `WeatherTool`, `ExchangeRateTool`).
- Map model output to `TravelPlanResponse`.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TravelAgentServiceTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/config src/main/java/com/example/bedrockagent/travel src/test/java/com/example/bedrockagent/travel/service
git commit -m "feat: orchestrate travel planning with bedrock tool calling"
```

---

### Task 9: Add partial-failure behavior and assumptions population

**Files:**
- Modify: `src/main/java/com/example/bedrockagent/travel/service/TravelAgentService.java`
- Test: `src/test/java/com/example/bedrockagent/travel/service/TravelAgentPartialFailureTest.java`

**Step 1: Write the failing test**

```java
class TravelAgentPartialFailureTest {

    @Test
    void includes_assumptions_when_weather_fails() {
        // mock weather tool exception
        // assert response.assumptions contains weather fallback note
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TravelAgentPartialFailureTest"`
Expected: FAIL because fallback not implemented

**Step 3: Write minimal implementation**

Add targeted exception handling for non-core tool failures and enrich `assumptions` + `weatherAlerts` as needed.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TravelAgentPartialFailureTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/travel/service src/test/java/com/example/bedrockagent/travel/service
git commit -m "feat: support partial success when non-core tools fail"
```

---

### Task 10: Add structured logging and trace filter integration

**Files:**
- Modify: `src/main/java/com/example/bedrockagent/common/trace/TraceIdFilter.java`
- Modify: `src/main/java/com/example/bedrockagent/travel/service/TravelAgentService.java`
- Test: `src/test/java/com/example/bedrockagent/common/trace/TraceIdFilterTest.java`

**Step 1: Write the failing test**

```java
class TraceIdFilterTest {

    @Test
    void adds_trace_id_header_and_mdc() {
        // verify X-Trace-Id header exists
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TraceIdFilterTest"`
Expected: FAIL because behavior incomplete

**Step 3: Write minimal implementation**

Ensure `traceId` is created when absent, attached to MDC/request/response, and service logs tool start/end/fail with latency.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TraceIdFilterTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/common/trace src/main/java/com/example/bedrockagent/travel/service src/test/java/com/example/bedrockagent/common/trace
git commit -m "feat: add trace id and tool call structured logging"
```

---

### Task 11: Add OpenAPI examples and demo-ready docs

**Files:**
- Modify: `src/main/java/com/example/bedrockagent/travel/api/TravelPlanController.java`
- Modify: `src/main/resources/application.yml`
- Create: `README.md`
- Test: `src/test/java/com/example/bedrockagent/travel/api/TravelPlanOpenApiTest.java`

**Step 1: Write the failing test**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelPlanOpenApiTest {

    @Autowired TestRestTemplate rest;

    @Test
    void swagger_endpoint_is_available() {
        var body = rest.getForObject("/v3/api-docs", String.class);
        assertThat(body).contains("/api/v1/travel/plan");
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TravelPlanOpenApiTest"`
Expected: FAIL because OpenAPI not configured

**Step 3: Write minimal implementation**

Add springdoc dependency/config and request/response examples on endpoint; include run instructions and env vars in README.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TravelPlanOpenApiTest"`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/java/com/example/bedrockagent/travel/api src/main/resources/application.yml README.md src/test/java/com/example/bedrockagent/travel/api
git commit -m "docs: add swagger examples and demo run guide"
```

---

### Task 12: End-to-end verification checklist

**Files:**
- Create: `scripts/demo-smoke.sh`
- Create: `src/test/java/com/example/bedrockagent/e2e/TravelPlanE2ETest.java`

**Step 1: Write the failing test**

```java
class TravelPlanE2ETest {

    @Test
    void happy_path_response_contains_required_fields() {
        // call endpoint with test doubles or test profile
        // assert summary/itinerary/budget/assumptions present
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*TravelPlanE2ETest"`
Expected: FAIL because E2E wiring not complete

**Step 3: Write minimal implementation**

Complete test profile wiring + smoke script that checks app health and makes one sample request.

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*TravelPlanE2ETest" && bash scripts/demo-smoke.sh`
Expected: PASS and sample response with required fields

**Step 5: Commit**

```bash
git add src/test/java/com/example/bedrockagent/e2e scripts/demo-smoke.sh
git commit -m "test: add end-to-end verification for demo scenario"
```

---

## Final verification before handoff

Run:
- `./gradlew clean test`
- `./gradlew bootRun --args='--spring.profiles.active=local'`
- `curl -X POST http://localhost:8080/api/v1/travel/plan -H 'Content-Type: application/json' -d '{"origin":"Seoul","destination":"Tokyo","days":4,"budgetKrw":1500000,"preferences":"food and shopping"}'`

Expected:
- All tests pass.
- Swagger UI opens at `/swagger-ui/index.html`.
- Response includes `summary`, `itinerary`, `budget`, `weatherAlerts`, `assumptions`, `traceId`.
