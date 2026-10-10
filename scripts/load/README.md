# 부하 테스트

## 사전 준비

- `brew install k6 jq`
- `docker compose up -d` 후 서비스가 8080 응답

### 브랜치 전환 시

브랜치마다 서비스 코드가 다르므로, 전환 후에는 이미지를 새로 생성하여 도커 컨테이너를 재실행한다.

```bash
git checkout <branch>
./gradlew --stop && ./gradlew jibDockerBuild && docker compose up -d
```

## part-2

```bash
./scripts/load/part-2/run.sh
```

`run.sh` 는 `reset → create_coupon → k6 → verify` 를 한 번에 실행한다.

## part-3

```bash
./scripts/load/part-3/run.sh           # 워밍업 1회 + 본 측정 1회 (기본)
./scripts/load/part-3/run.sh --once    # 한 회차만
```

`run.sh` 는 `reset → create_coupon → k6 → verify_burst` 를 한 번에 묶고, 기본으로 두 회차를 도는 워밍업 절차까지 자동화한다. 
서비스 프로세스는 띄운 채로 두고, 회차 사이에 `reset.sh` 로 DB/Redis 만 비우므로 JVM JIT, HikariCP 풀, Lettuce/Kafka 컨슈머 상태는 살아남아 2회차가 steady-state 값이 된다. 
3-1 (동기) 만 예외로 두 회차가 비슷한데, 병목이 JIT/풀이 아니라 DB INSERT 자체라 워밍업이 의미 없기 때문이다.

