# travel-planner

Spring AI + AWS Bedrock 기반 Tool Calling 여행 플래너 데모입니다.

## 요구 사항
- Java 21
- AWS Bedrock 접근 권한(Converse API)
- Places / Weather / FX API 키(선택)

## 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## API
- OpenAPI JSON: `/v3/api-docs`
- Swagger UI: `/swagger-ui/index.html`
- Endpoint: `POST /api/v1/travel/plan`

요청 예시:

```json
{
  "origin": "Seoul",
  "destination": "Tokyo",
  "days": 4,
  "budgetKrw": 1500000,
  "preferences": "food and shopping"
}
```

## 환경 변수
- `AWS_REGION`
- `BEDROCK_MODEL_ID`
- `PLACES_BASE_URL`
- `WEATHER_BASE_URL`
- `FX_BASE_URL`
