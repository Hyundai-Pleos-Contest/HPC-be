# Database migrations

`postgres` 프로필에서만 Flyway를 실행한다. 초기 설정에는 도메인 테이블과 SQL migration을 넣지 않는다.

스키마가 합의되면 `V1__설명.sql` 형식으로 첫 migration을 추가한다. 적용된 migration은 수정하지 않고 다음 버전으로 변경한다. 빈 개발 DB를 사용하며 `baseline-on-migrate`와 자동 테이블 생성은 켜지 않는다.
