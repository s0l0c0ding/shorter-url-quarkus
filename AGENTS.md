# AGENTS.md

## Commands

```bash
# Dev mode (requires MongoDB container first)
docker run --name mongodb -d mongo:4.2-bionic
./mvnw quarkus:dev

# Unit tests only (no container needed)
./mvnw test

# All tests (integration tests use Testcontainers automatically)
./mvnw verify

# Package JVM
./mvnw package

# Native build (requires GraalVM or container support)
./mvnw package -Pnative -Dquarkus.native.containerBuild=true
```

## Architecture

- **Stack**: Quarkus 3.34.1, MongoDB (Panache), RESTEasy JSON-B
- **Java**: 21
- **Package**: `dev.usercoding`
- **Security**: Elytron embedded properties (`src/main/resources/application.properties`)
  - User `user` / `fake1234` has roles `admin,user`
  - User `user1` / `fake1234` has role `user`

## Testing

- Unit tests: Standard JUnit 5 + Mockito in `src/test/java`
- Integration tests: Use Testcontainers (MongoDbContainer in `conftest`)
- Testcontainers starts a real MongoDB automatically via `QuarkusTestResourceLifecycleManager`
- No manual Docker setup needed for tests

## IP Geolocation Data

The app requires an `Ip` collection with a compound index on `ipfrom` and `ipto`. See `.env_sec` for:
- Import command (`mongoimport` with CSV)
- Cleanup query (delete where `countryname: "-"`)
- Index creation command (`createIndex`)

## GCP Deployment

- Triggered on GitHub release creation
- Builds native image: `mvn package -Pnative -DskipTests`
- Uses `src/main/docker/Dockerfile.native`
- Pushes to `gcr.io/<PROG_ID>/shorter-url:<version>`

## Docker Build Gotcha

If building locally with multi-stage Docker:
- Must comment out or remove content in `.dockerignore` (it filters `src/` out by default)
- Use: `docker build -f src/main/docker/Dockerfile.multistage`

## Config Source

Real runtime config is in `src/main/resources/application.properties`. Dev credentials are in `.env_sec` (do not commit).

## Migration Notes (Quarkus 2.x → 3.x)

- Jakarta EE namespace: `javax.*` → `jakarta.*`
- Security extension: `quarkus-security` → `quarkus-elytron-security-properties-file`