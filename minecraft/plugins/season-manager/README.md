# SeasonManager Plugin (Core Manager)

`season-manager`는 서버 시즌 운영 시스템의 코어 관리자 플러그인입니다.

## Role
- 시즌 상태/라운드/생명/점수/보더 등 운영 제어
- 카드 정보 호출/관리 및 정책 적용
- 운영 명령(`season`, `slot`, `life`, `score`, `border`, `information`) 제공

## Runtime relation
- `card-draw`: 카드 추첨/리롤/강화 및 효과 발동 경로
- `card-engine`: 런타임 효과 루프 오케스트레이션(delegate)
- `season-manager`: 상위 운영/관리 컨트롤 플레인

## Docs
시즌 카드 설계 문서는 계속 `src/main/resources/season_1/*`에 함께 유지합니다.
