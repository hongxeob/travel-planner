# Spring AI + AWS Bedrock Tool Calling Travel Planner Design

## 1) 목표
- 새 레포지토리에서 로컬 실행 가능한 데모를 만든다.
- Spring AI + AWS Bedrock(Claude) 기반 Tool Calling Agent를 구현한다.
- 실제 외부 API(Places, Weather, Exchange Rate)를 호출해 여행 계획을 생성한다.
- REST API + Swagger UI로 시연 가능해야 한다.

## 2) 범위
### In Scope
- `POST /api/v1/travel/plan` 엔드포인트
- Tool 3종 연동
  - 장소 검색
  - 날씨 조회
  - 환율 조회
- Agent 응답 포맷
  - `summary`, `itinerary`, `budget`, `weatherAlerts`, `assumptions`
- 기본 로깅/traceId/표준 에러 응답
- 단위/통합 테스트 + 데모 시나리오

### Out of Scope
- AWS 배포
- 사용자 인증/권한
- 장기 메모리(RAG, 벡터DB)
- 프론트엔드 UI

## 3) 아키텍처
요청은 `TravelPlanController`가 받고, `TravelAgentService`가 Bedrock Claude에 프롬프트를 전달한다. 모델은 Spring AI Tool Calling을 통해 `PlaceSearchTool`, `WeatherTool`, `ExchangeRateTool`을 선택/호출한다. Tool 결과를 종합해 최종 여행 플랜(JSON 구조)을 반환한다.

```text
Client -> REST Controller -> TravelAgentService -> Bedrock Claude
                                        |-> PlaceSearchTool -> Places API
                                        |-> WeatherTool     -> Weather API
                                        |-> ExchangeRateTool-> FX API
```

## 4) 컴포넌트 설계
- `TravelPlanController`
  - 입력 DTO validation
  - 서비스 호출 및 응답 반환
- `TravelAgentService`
  - 시스템 프롬프트/응답 포맷 제약
  - ChatClient + tools 연결
- `PlaceSearchTool`
  - 여행지 후보/좌표/기본 정보 조회
- `WeatherTool`
  - 여행 기간 기준 날씨 조회
- `ExchangeRateTool`
  - 예산 환산 기준 환율 조회
- `External API Clients`
  - 각 API WebClient 호출
  - timeout/retry/에러 매핑

## 5) 데이터 흐름
1. 사용자 자연어 요청 + 구조화 입력 전달
2. 모델이 필요한 Tool 선택
3. 장소 -> 날씨 -> 환율 순으로 데이터 확보(모델이 결정)
4. 모델이 일정/예산/주의사항 종합
5. API 응답 반환

## 6) 응답 계약
- `summary`: 요약 문장
- `itinerary[]`: 일자별 추천 일정
- `budget`: 원화+현지통화 예산
- `weatherAlerts[]`: 준비물/주의사항
- `assumptions[]`: 데이터 누락/가정
- `traceId`: 관측성 식별자

## 7) 에러 처리 정책
- 핵심 실패(장소 조회 실패): 요청 실패 처리
- 비핵심 실패(날씨/환율 일부 실패): 부분 성공 응답 + assumptions 명시
- 표준 에러 포맷: `code`, `message`, `traceId`, `retryable`

## 8) 보안
- 모든 API 키는 환경변수로 주입
- 로그에 비밀정보 마스킹
- 입력 검증(기간/예산/필수 필드)
- Tool 범위 제한(허용된 API만 호출)

## 9) 관측성
- 구조화 로그(`traceId`, tool명, latency, status)
- Tool 호출 이벤트 로그(START/END/FAIL)
- `/actuator/health` 사용

## 10) 테스트 전략
- Tool 단위 테스트(정상/실패/타임아웃)
- API Client WireMock 계약 테스트
- Controller 통합 테스트
- 부분 실패 시나리오 테스트
- 실제 API E2E 스모크 테스트(로컬)

## 11) 데모 시나리오
입력 예:
- 출발지: 서울
- 목적지: 도쿄
- 기간: 3박 4일
- 예산: 1,500,000 KRW
- 선호: 맛집/쇼핑

기대:
- 도구 호출 로그가 보이고,
- 최종 응답에 일정/예산/날씨 주의사항이 포함된다.
