# my-msa-common

Troica Market MSA 전체에서 공유하는 **공통 라이브러리**. GitHub Packages Maven 레포지토리에 배포되어 각 서비스가 의존성으로 가져간다.

## 제공 내용

| 패키지 | 클래스/파일 | 역할 |
|--------|------------|------|
| `exception` | `CustomException` | HTTP status + code + message를 가지는 기본 예외 클래스. gRPC/REST 양쪽에서 사용 |
| `domain.entity` | `BaseEntity`, `BaseDomainEntity`, `BaseOrmEntity` | createdAt/updatedAt 등 공통 필드 |
| `adapter.configuration` | `JPAConfig`, `QueryDslConfig` | JPA + QueryDSL 설정 |
| `adapter.infrastructure.kafka` | `EventMapper` | Kafka 이벤트 직렬화 공통 처리 |
| `adapter.infrastructure.jpa` | `EntityMapper` | JPA 엔티티 ↔ 도메인 엔티티 변환 공통 처리 |
| `util.time` | `DateTimeUtil`, `MicrosecondTruncatingClock` | 날짜/시간 유틸 |
| `util.backoff` | `CalcBackoff` | 재시도 백오프 계산 |

## CustomException 구조

각 서비스는 `CustomException`을 상속해 도메인별 예외를 정의한다.

```kotlin
open class CustomException(
    val code: String,
    override val message: String,
    val status: Int,        // HTTP status code
) : RuntimeException(message)
```

예시 (product-service):
```kotlin
class NoSuchProductException : CustomException(
    code = "PRODUCT_NOT_FOUND",
    message = "상품을 찾을 수 없습니다",
    status = 404
)
```

서비스에서 던지면:
- **gRPC 서버**: `GrpcExceptionHandler`가 `status`를 보고 gRPC `Status.NOT_FOUND` 등으로 변환
- **user-api-gateway**: `GlobalExceptionHandler`가 gRPC status를 HTTP 상태 코드로 변환

## 배포 방식 (GitHub Packages)

```
Tag push (vX.X.X)
  → GitHub Actions (.github/workflows/publish.yml)
  → ./gradlew :common:publish -PpublishVersion=X.X.X
  → https://maven.pkg.github.com/kjylab/my-msa-common
```

### 의존성 추가 (다른 서비스에서 사용)

`settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/kjylab/my-msa-common")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("PACKAGES_TOKEN") ?: ""
            }
        }
    }
}
```

> GitHub Packages는 공개 레포여도 읽기에 인증이 필요하다. CI에서는 `GH_PAT` 시크릿을 `PACKAGES_TOKEN` 환경변수로 주입한다.

`build.gradle.kts`:
```kotlin
implementation("com.github.kjylab:my-msa-common:1.0.8")
```

## 로컬 버전 확인

```bash
# GitHub Packages에서 사용 가능한 버전 목록
gh api /users/kjylab/packages/maven/my-msa-common/versions --jq '.[].name'
```
