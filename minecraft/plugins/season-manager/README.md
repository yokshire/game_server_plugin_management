# Season Manager (Info Archive)

이 경로는 **실행 플러그인 코드가 아니라 시즌 카드 설계/검토 문서(info) 보관용**으로 유지합니다.

주요 문서:
- `src/main/resources/season_1/adjust_card.md`
- `src/main/resources/season_1/card_pool_80_review.md`
- `src/main/resources/season_1/effect_description_audit_80.md`
- `src/main/resources/season_1/effect_impl_audit_80.md`

실행 플러그인 분리:
- 카드풀 추첨/적용: `minecraft/plugins/card-draw`
- 런타임 엔진(기믹 tick 위임): `minecraft/plugins/card-engine`

이 구조에서 시즌 카드 info 문서는 계속 이 `season-manager` 경로를 단일 출처로 사용합니다.
