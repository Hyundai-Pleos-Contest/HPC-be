# HPC-be 작업 지침

## 목적과 범위

- 현대차 Pleos 공모전 프로젝트의 백엔드 저장소다. Java 21, Spring Boot, Gradle을 사용한다.
- 초기 기반에는 애플리케이션 실행, Actuator health, 선택적 PostgreSQL/Flyway만 있다. 기능·인증·도메인 모델은 요구사항 합의 후 추가한다.
- 코드 작업 전 `README.md`에서 실행 환경을 확인한다. 브랜치·커밋·PR 작업 전 `CONTRIBUTING.md`를 읽는다.

## 명령

이 저장소 루트에서 실행한다. Windows는 `.\gradlew.bat`, macOS/Linux는 `sh ./gradlew`를 사용한다.

- 로컬 실행: `.\gradlew.bat bootRun`
- 검증: `.\gradlew.bat spotlessCheck test bootJar`
- Java 포맷: `.\gradlew.bat spotlessApply`
- 의도적인 의존성 갱신: `.\gradlew.bat dependencies --write-locks` 후 검증

## 구현 규칙

- 기본 실행과 테스트는 PostgreSQL·AI 서비스 없이 성공해야 한다. DB는 `postgres` 프로필에서만 활성화한다.
- Java 포맷은 `build.gradle`의 Spotless/google-java-format 설정을 따른다.
- 적용된 Flyway migration을 수정하지 않고 후속 버전을 추가한다. 자동 schema 생성·DB 초기화 기능을 임의로 도입하지 않는다.
- AI 주소는 `AI_BASE_URL` 설정을 사용한다. 실제 AI client와 API 계약은 요구사항이 정해진 후 구현한다.
- 실제 비밀정보·`.env`·서명 키·IDE/SDK 개인 경로는 커밋하지 않는다.
- 변경과 직접 관련된 테스트를 수행하고 실행한 명령과 결과를 보고한다. 실행하지 못한 검증은 이유와 함께 구분한다.
- 다른 레포와 맞물리는 변경은 관련 API 계약과 `HPC-workspace` 통합 문서를 확인하되, 형제 폴더의 존재를 빌드 전제조건으로 만들지 않는다.
