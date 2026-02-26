# Plugin Split Note (card_engine / card_draw)

## 목표
- `card_engine`: 런타임 엔진(기믹 tick, 보더/추격자/오라 엔진 루프)
- `card_draw`: 카드풀 호출, 추첨/리롤/티어강화, 상태/GUI/명령
- `season-manager`: 시즌 카드 info 문서 단일 출처 유지

## 1차 구현 상태
- `minecraft/plugins/card-draw` 생성
  - 기존 SeasonManager 코드 기반
  - `engine.runtime_delegated_to_card_engine` 설정 추가
  - delegation hook 공개
    - `isCardEngineDelegationActive()`
    - `runCardEngineCoreTickDelegated()`
- `minecraft/plugins/card-engine` 생성
  - CardDraw hook를 reflection으로 주기 호출
  - split 모드에서 서브시스템 단위 호출 지원
    - active_effects / stalker / raid / aura / score_decay / border_wither
  - `/cardengine status`, `/cardengine reload` 제공
- info 문서는 기존 위치 유지
  - `minecraft/plugins/season-manager/src/main/resources/season_1/*`

## 현재 동작 방식
- CardDraw 내부 tick에서 draw/상태 로직은 계속 실행
- CardDraw 설정 `engine.runtime_delegated_to_card_engine: true` + CardEngine 활성화 시,
  런타임 엔진 코어 루프는 CardEngine 호출 경로로 실행

## 다음 단계(완전 분리)
1. CardDraw 내부 엔진 하위 메서드의 실제 구현을 CardEngine으로 이관
2. 공유 상태 계약(API 또는 DB 스키마) 확정
3. CardDraw는 카드 상태 write-only, CardEngine은 runtime apply-only로 분리
4. season-manager 코드 폴더는 info-only로 고정
