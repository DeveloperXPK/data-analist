# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start local database only (for IDE development)
docker compose -f docker-compose.dev.yml up -d

# Run the app locally (requires .env sourced or env vars set)
./mvnw spring-boot:run

# Compile
./mvnw clean compile -B

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=AuthServiceTest

# Run a single test method
./mvnw test -Dtest=AuthServiceTest#register_WithValidData_ShouldReturnUserResponse

# Package (skip tests)
./mvnw package -DskipTests -B

# SonarQube analysis (requires SONAR_HOST_URL and SONAR_TOKEN env vars)
./mvnw sonar:sonar

# Build Docker image
docker build -t dataanalist:local .

# Start full stack (app + postgres) — requires .env file
docker compose up -d
```

## Environment

Copy `.env.example` to `.env` and fill in values before running locally. Required variables:

- `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME` — PostgreSQL credentials
- `JWT_SECRET` — Base64-encoded 256-bit key (`openssl rand -base64 32`)
- `JWT_EXPIRATION_MS` — Token TTL in milliseconds (default: 86400000)

The `application-test` profile (used by all tests) uses H2 in-memory with Flyway disabled — no `.env` needed for tests.

## Architecture

**Spring Boot 4.1.0, Java 21 LTS, Maven. Classic layered architecture.**

```
co.ingedev.dataAnalist/
├── config/       Spring beans: SecurityConfig, OpenApiConfig, AppProperties
├── controller/   REST layer — AuthController, CreditStudyController, AdminUserController
├── dto/          Java records only — request/ and response/ subpackages
├── entity/       JPA entities: User (implements UserDetails), CreditStudy
├── enums/        Role (ADMIN, USER), StudyStatus (PENDIENTE, APROBADO, RECHAZADO)
├── exception/    GlobalExceptionHandler + 3 custom RuntimeExceptions
├── repository/   Spring Data JPA interfaces — no native SQL
├── security/     JWT layer: JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
└── service/      Interface + Impl pairs for Auth, CreditStudy, User
```

**Database migrations** live in `src/main/resources/db/migration/` as Flyway scripts (`V{n}__{description}.sql`). Production uses `spring.jpa.hibernate.ddl-auto=validate`.

## Key Patterns

**JWT flow**: `JwtAuthenticationFilter` (OncePerRequestFilter) extracts the Bearer token, validates it via `JwtTokenProvider`, then loads `UserDetails` from DB and sets the `SecurityContext`. The filter is stateless — no session is created.

**Role-based data access**: Logic for "ADMIN sees all / USER sees only their own" lives in `CreditStudyServiceImpl`, not in controllers. Controllers extract `Authentication` from `SecurityContextHolder` and pass it to the service.

**AppProperties**: JWT config is bound via `@ConfigurationProperties(prefix = "app")` — `app.jwt-secret` and `app.jwt-expiration-ms`. `@EnableConfigurationProperties(AppProperties.class)` is declared in `SecurityConfig`.

**Error responses**: `GlobalExceptionHandler` always returns `ApiError` (record with timestamp, status, message, path). Stack traces are never serialized to responses. The custom `AuthenticationEntryPoint` and `AccessDeniedHandler` in `SecurityConfig` also return `ApiError` JSON (never HTML).

**Tests**: Unit tests use `@ExtendWith(MockitoExtension.class)` with no Spring context. Controller tests use `@WebMvcTest` + `@Import(SecurityConfig.class)` + `@WithMockUser`. All tests activate `@ActiveProfiles("test")` which switches to H2 and disables Flyway.

## CI/CD

`Jenkinsfile` defines 9 stages: Checkout → Build → Test → Package → SonarQube Analysis → Quality Gate → Docker Build → Docker Push → Deploy to VPS.

Deploy stage only runs on `main` branch via SSH (`sshagent`). Required Jenkins credentials: `dockerhub-credentials`, `sonar-token`, `vps-ssh-key`, `vps-host`. The Jenkins tool names must match: `Maven-3.9` and `JDK-21`.

SonarQube server must be configured in `Manage Jenkins > Configure System` with the name `SonarQube`.
