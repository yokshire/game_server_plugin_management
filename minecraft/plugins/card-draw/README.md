# CardDraw Plugin

카드풀 추첨/리롤/티어강화 및 시즌 상태(UI/명령)를 담당합니다.

## Responsibility boundary
- `CardDraw`: 카드 정보 호출/관리, 추첨/리롤/강화, 상태/명령
- `CardEngine`: 런타임 효과 tick 오케스트레이션
- `SeasonManager`: 서버 운영 코어 관리자(상위 컨트롤 플레인)

## Runtime delegation
- `config.yml`의 `engine.runtime_delegated_to_card_engine: true` 일 때,
  런타임 기믹 tick은 내부 tick에서 실행하지 않고 `CardEngine` 플러그인 위임 호출로 실행됩니다.
- `CardEngine`이 비활성/미설치면 자동으로 로컬 실행으로 폴백합니다.
- 위임 훅:
  - `runCardEngineCoreTickDelegated()`
  - `runCardEngineSubsystemTickDelegated(String subsystemKey)`

## Build
```bash
cd minecraft/plugins/card-draw
mkdir -p build/classes
CP=$(find /home/yyg/server/big_world/paper/season_1_end/libraries -name '*.jar' -printf '%p:' | sed 's/:$//')
javac -proc:none -encoding UTF-8 -source 21 -target 21 -cp "$CP" -d build/classes $(find src/main/java -name '*.java')
rsync -a src/main/resources/ build/classes/
jar cf build/card-draw-0.1.0.jar -C build/classes .
```
