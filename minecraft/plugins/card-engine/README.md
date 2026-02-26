# CardEngine Plugin

CardDraw의 런타임 엔진 tick(기믹/보더/추격자/오라 관련 루프)을 외부에서 오케스트레이션하는 엔진 플러그인입니다.

## Delegate model
- 대상 플러그인(`CardDraw`)의 공개 메서드를 reflection으로 호출합니다.
  - `isCardEngineDelegationActive()`
  - `runCardEngineCoreTickDelegated()`
  - `runCardEngineSubsystemTickDelegated(String subsystemKey)` (지원 시)
- 호출 주기와 대상 이름은 `config.yml`의 `delegate.*`로 조절합니다.
- `delegate.mode: split`이면 하위 엔진 루프를 서브시스템 단위로 분리 실행합니다.
  - `active_effects`, `stalker`, `raid`, `aura`, `score_decay`, `border_wither`
- 대상 플러그인이 서브시스템 훅을 지원하지 않으면 자동으로 `core` 모드로 폴백합니다.

## Commands
- `/cardengine status`
- `/cardengine reload`

## Build
```bash
cd minecraft/plugins/card-engine
mkdir -p build/classes
CP=$(find /home/yyg/server/big_world/paper/season_1_end/libraries -name '*.jar' -printf '%p:' | sed 's/:$//')
javac -encoding UTF-8 -source 21 -target 21 -cp "$CP" -d build/classes $(find src/main/java -name '*.java')
rsync -a src/main/resources/ build/classes/
jar cf build/card-engine-0.1.0.jar -C build/classes .
```
