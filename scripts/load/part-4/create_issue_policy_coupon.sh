#!/usr/bin/env bash
# 쿠폰 정보 캐시 실험용 쿠폰을 만들고 ID를 출력한다.
# 시작 시각을 미래로 설정해 issue API가 쿠폰 정보 조회 후 NotStarted로 끝나도록 한다.
# 쿠폰 날짜를 미래로 하여 쿠폰서비스에서 NotStartedException()이 터지도록하여 이후 로직말고 오직 쿠폰을 디비에서 조회하는지 캐시에서 조회하는지 검증

set -euo pipefail

curl -fsS -X POST http://localhost:8080/api/coupons \
  -H 'Content-Type: application/json' \
  -d '{"name":"issue policy cache test","totalQuantity":1,"validityDays":7,"startsAt":"2099-01-01T00:00:00"}' \
  | jq -r '.id'


