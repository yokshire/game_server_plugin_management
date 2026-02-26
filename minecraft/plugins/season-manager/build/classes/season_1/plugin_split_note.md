# Plugin Split Note (season_manager / card_draw / card_engine)

## 목표
- `season-manager`: 서버 운영 코어 관리자(라운드/점수/라이프/보더/정책)
- `card-draw`: 카드 추첨/리롤/강화와 효과 발동 경로
- `card-engine`: 런타임 효과 루프 오케스트레이션(delegate)

## 현재 동작 방식
- SeasonManager가 운영 제어(Command + State)를 담당
- CardDraw가 카드 런타임 로직을 수행
- CardEngine은 CardDraw 공개 hook를 주기 호출해 엔진 루프를 분할 실행 가능

## 구현 상태
- `minecraft/plugins/card-draw`:
  - 기존 SeasonManager 코드 기반 분리본
  - `engine.runtime_delegated_to_card_engine` 설정 지원
  - delegation hook:
    - `isCardEngineDelegationActive()`
    - `runCardEngineCoreTickDelegated()`
    - `runCardEngineSubsystemTickDelegated(String subsystemKey)`
- `minecraft/plugins/card-engine`:
  - CardDraw hook reflection 호출
  - split 서브시스템:
    - active_effects / stalker / raid / aura / score_decay / border_wither
  - `/cardengine status`, `/cardengine reload`
- `minecraft/plugins/season-manager`:
  - 코어 운영 관리자 플러그인 유지
  - 시즌 카드 문서(`src/main/resources/season_1/*`)도 이 경로에 유지

## 운영 메모
- 세 플러그인의 책임은 분리하되, SeasonManager를 문서 전용으로 취급하지 않는다.
- 실제 배포 조합은 서버 역할(운영/게임플레이)에 따라 결정한다.
