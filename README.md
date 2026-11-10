# HPC-be

현대차 Pleos 공모전 프로젝트의 백엔드 개발 환경이다. 현재는 Spring Boot 실행, 상태 확인, 선택적 PostgreSQL 연결 기반만 포함한다. 서비스 기능·인증·도메인 모델은 아직 정의하지 않았다.

## 개발 환경

| 항목 | 고정 버전 |
| --- | --- |
| JDK | 21 |
| Spring Boot | 3.5.16 |
| Gradle Wrapper | 8.14.6 |
| Spotless | 8.11.0 |
| google-java-format | 1.28.0 |

JDK 21을 설치하고 `JAVA_HOME` 또는 PATH에 연결한다. Gradle은 별도 설치하지 않고 저장소의 Wrapper를 사용한다. 첫 실행에는 의존성 다운로드를 위한 인터넷 연결이 필요하다. 프로젝트 의존성은 `gradle.lockfile`, Gradle 배포 파일은 Wrapper의 SHA-256으로 고정한다.

Windows의 한글 경로에서 테스트 worker가 클래스를 찾지 못하는 [Gradle 8 인코딩 이슈](https://github.com/gradle/gradle/issues/30304)에 대응하기 위해 Gradle JVM만 `file.encoding=COMPAT`를 사용한다. Java 소스·포맷·테스트 런타임은 `build.gradle`에서 UTF-8로 명시한다. 개인 경로나 Windows 시스템 설정 변경은 필요하지 않다.

## 로컬 실행

저장소 루트에서 실행한다. 기본 프로필에는 PostgreSQL이나 AI 서버가 필요하지 않다.

```powershell
.\gradlew.bat bootRun
```

macOS/Linux에서는 `sh ./gradlew bootRun`을 사용한다.

다른 터미널에서 확인한다.

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

정상 응답은 `{"status":"UP"}`이다. 종료는 실행한 터미널에서 `Ctrl+C`를 누른다.

## 검증과 포맷

```powershell
.\gradlew.bat spotlessCheck test bootJar
.\gradlew.bat spotlessApply
```

첫 명령은 포맷 확인, DB 없이 HTTP health endpoint 실행 검증, 실행 가능한 JAR 생성까지 수행한다. 두 번째 명령은 Java 파일을 포맷한다. Linux/macOS에서는 `.\gradlew.bat` 대신 `sh ./gradlew`를 사용한다. 빌드 결과는 `build/libs/hpc-be.jar`, 테스트 보고서는 `build/reports/tests/test/index.html`이다.

패키징한 앱은 `java -jar build/libs/hpc-be.jar`로 실행할 수 있다. 이 경우에도 기본 포트와 프로필은 동일하다.

의존성을 의도적으로 변경한 경우 `dependencies --write-locks`와 검증 명령을 실행하고 lockfile 변경을 함께 리뷰한다. CI는 `CI` workflow의 `verify` job에서 같은 검증을 수행한다.

## 환경변수

| 변수 | 기본값/역할 |
| --- | --- |
| `SERVER_PORT` | `8080` |
| `AI_BASE_URL` | `http://localhost:8000`; 향후 AI client를 위한 주소 설정이며 현재 네트워크 호출은 없다. |
| `SPRING_PROFILES_ACTIVE` | 미설정 시 DB 없이 실행. `postgres`로 지정하면 DB 연결 활성화. |
| `DB_URL` | PostgreSQL 프로필에서 `jdbc:postgresql://localhost:5432/hpc` |
| `DB_USERNAME` | PostgreSQL 프로필에서 `hpc` |
| `DB_PASSWORD` | PostgreSQL 프로필에서는 명시적으로 설정해야 한다. |

`.env.example`은 변수 목록이다. **Spring Boot는 `.env`를 자동으로 읽지 않는다.** 셸 환경변수, IDE 실행 설정 또는 Docker Compose의 `environment`/`env_file`로 전달한다. 실제 비밀번호와 개인 설정은 커밋하지 않는다.

## 선택적 PostgreSQL / Flyway

실행 중인 개발용 PostgreSQL과 빈 `hpc` 데이터베이스를 준비한 후 실행한다. 전체 시스템의 PostgreSQL 실행 방법은 `HPC-workspace`에서 관리한다.

```powershell
$env:SPRING_PROFILES_ACTIVE = 'postgres'
$env:DB_URL = 'jdbc:postgresql://localhost:5432/hpc'
$env:DB_USERNAME = 'hpc'
$env:DB_PASSWORD = 'hpc-local-only'
.\gradlew.bat bootRun
```

예제 비밀번호는 로컬 개발용이다. Flyway는 해당 프로필에서만 동작하고, 현재는 도메인 SQL migration이 없다. 최초 실제 migration은 스키마 합의 후 `src/main/resources/db/migration`에 추가한다. 활성화된 DB에 연결하지 못하면 시작 또는 health 확인이 실패하는 것이 정상이다.

다시 DB 없이 실행하려면 현재 터미널의 `SPRING_PROFILES_ACTIVE`를 해제한다.

```powershell
Remove-Item Env:SPRING_PROFILES_ACTIVE
```

## 컨테이너

Docker가 실행 중인 환경에서 저장소 루트에서 사용한다.

```powershell
docker build -t hpc-be:local .
docker run --rm -p 8080:8080 hpc-be:local
```

JDK/JRE 21 기반의 다단계 빌드를 사용하며 애플리케이션은 일반 사용자로 실행한다. 위 명령은 DB 없이 실행한다. 컨테이너에서 다른 서비스에 연결할 때는 `localhost` 대신 Compose 서비스 이름 등 실제 접근 주소를 지정한다.

## 협업과 AI 지침

브랜치·커밋·PR 규칙은 `CONTRIBUTING.md`, Codex와 Claude Code의 공통 작업 지침은 `AGENTS.md`를 따른다. `CLAUDE.md`는 공통 지침을 가져오는 연결 파일이다.

## 버전 근거

- [Spring Boot 3.5의 Java·Gradle 지원 범위](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
- [Gradle 8.14.6 릴리스](https://docs.gradle.org/8.14.6/release-notes.html)
- [Spotless Gradle Plugin](https://plugins.gradle.org/plugin/com.diffplug.spotless)
