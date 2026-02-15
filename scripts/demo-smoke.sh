#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
APP_PID=""

cleanup() {
  if [ -n "${APP_PID}" ] && kill -0 "${APP_PID}" 2>/dev/null; then
    kill "${APP_PID}" >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

if ! curl -fsS "${BASE_URL}/actuator/health" >/dev/null 2>&1; then
  "${PROJECT_ROOT}/gradlew" -p "${PROJECT_ROOT}" bootRun --args='--spring.profiles.active=local' >/tmp/travel-planner-boot.log 2>&1 &
  APP_PID=$!

  for _ in {1..60}; do
    if curl -fsS "${BASE_URL}/actuator/health" >/dev/null 2>&1; then
      break
    fi
    sleep 1
  done
fi

curl -fsS "${BASE_URL}/actuator/health" >/dev/null

curl -fsS -X POST "${BASE_URL}/api/v1/travel/plan" \
  -H "Content-Type: application/json" \
  -d '{"origin":"Seoul","destination":"Tokyo","days":4,"budgetKrw":1500000,"preferences":"food and shopping"}' \
  | tee /tmp/travel-plan-response.json >/dev/null

for key in summary itinerary budget assumptions; do
  if ! grep -q "\"${key}\"" /tmp/travel-plan-response.json; then
    echo "missing key: ${key}"
    exit 1
  fi
done

echo "demo smoke test passed"
