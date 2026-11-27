package dev.solocoding.controller;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.solocoding.components.ratelimit.entity.RateLimitRecord;
import dev.solocoding.components.ratelimit.repository.RateLimitRepository;
import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.components.url.repository.UrlRepository;
import dev.solocoding.conftest.MongoDbContainer;
import dev.solocoding.exception.ServiceExceptionEnum;
import dev.solocoding.exception.error.ApiError;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
@TestHTTPEndpoint(PublicUrlController.class)
@QuarkusTestResource(MongoDbContainer.class)
class PublicUrlControllerIntegrationTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "p@ssword";

    @Inject
    UrlRepository urlRepository;

    @Inject
    RateLimitRepository rateLimitRepository;

    @BeforeEach
    void init() {
        urlRepository.deleteAll();
        rateLimitRepository.deleteAll();
    }

    @AfterEach
    void clean() {
        urlRepository.deleteAll();
        rateLimitRepository.deleteAll();
    }

    @Test
    // AC-3.1
    void shouldCreatePublicUrlWithSevenDayExpiration() {
        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");

        var actual = given()
                .auth().basic(USERNAME, PASSWORD)
                .header("X-Forwarded-For", "192.0.2.10")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .extract().as(UrlDto.class);

        var now = ZonedDateTime.now(ZoneOffset.UTC);
        var elapsed = Duration.between(now, actual.getExpireTime());
        assertTrue(elapsed.compareTo(Duration.ofDays(6).plusHours(23)) >= 0);
        assertTrue(elapsed.compareTo(Duration.ofDays(7).plusHours(1)) <= 0);
        assertNotNull(actual.getShortUrl());
        assertEquals(1, urlRepository.count());
    }

    @Test
    // AC-3.2
    void shouldIgnoreSuppliedExpirationForPublicUrl() {
        var suppliedExpiration = ZonedDateTime.now(ZoneOffset.UTC).plusDays(30);
        var body = "{\"fullUrl\":\"https://www.usercoding.dev\",\"expireTime\":\""
                + suppliedExpiration.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) + "\"}";

        var actual = given()
                .auth().basic(USERNAME, PASSWORD)
                .header("X-Forwarded-For", "192.0.2.11")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .post("/url")
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract().as(UrlDto.class);

        var elapsed = Duration.between(ZonedDateTime.now(ZoneOffset.UTC), actual.getExpireTime());
        assertTrue(elapsed.compareTo(Duration.ofDays(6).plusHours(23)) >= 0);
        assertTrue(elapsed.compareTo(Duration.ofDays(7).plusHours(1)) <= 0);
    }

    @Test
    // AC-3.3
    void shouldRejectInvalidPublicUrlWithoutCreatingLinkOrReservation() {
        var dto = new UrlDto();
        dto.setFullUrl("ftp://invalid.example");

        var actual = given()
                .auth().basic(USERNAME, PASSWORD)
                .header("X-Forwarded-For", "192.0.2.12")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().as(ApiError.class);

        assertEquals(ServiceExceptionEnum.NOT_VALID_URL.getCode(), actual.getCode());
        assertEquals(0, urlRepository.count());
        assertEquals(0, rateLimitRepository.count());
    }

    @Test
    // AC-3.4
    void shouldRejectSixthCreationWithinOneMinute() {
        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");

        for (int i = 0; i < 5; i++) {
            given()
                    .auth().basic(USERNAME, PASSWORD)
                    .header("X-Forwarded-For", "192.0.2.13")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .post("/url")
                    .then()
                    .statusCode(Status.CREATED.getStatusCode());
        }

        var actual = given()
                .auth().basic(USERNAME, PASSWORD)
                .header("X-Forwarded-For", "192.0.2.13")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.TOO_MANY_REQUESTS.getStatusCode())
                .extract().as(ApiError.class);

        assertEquals(7, actual.getCode());
        assertEquals(5, urlRepository.count());
    }

    @Test
    // AC-3.4
    void shouldCountExistingRateLimitRecordsAfterAtomicQuotaUpgrade() {
        var ip = "192.0.2.15";
        var createdAt = ZonedDateTime.now(ZoneOffset.UTC);
        for (int i = 0; i < 5; i++) {
            rateLimitRepository.persist(new RateLimitRecord(ip, createdAt));
        }

        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");

        given()
                .auth().basic(USERNAME, PASSWORD)
                .header("X-Forwarded-For", ip)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.TOO_MANY_REQUESTS.getStatusCode());

        assertEquals(0, urlRepository.count());
    }

    @Test
    // AC-3.5
    void shouldRejectEleventhCreationWithinOneDay() {
        var ip = "192.0.2.16";
        var createdAt = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(2);
        for (int i = 0; i < 10; i++) {
            rateLimitRepository.persist(new RateLimitRecord(ip, createdAt));
        }

        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");

        given()
                .auth().basic(USERNAME, PASSWORD)
                .header("X-Forwarded-For", ip)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.TOO_MANY_REQUESTS.getStatusCode());

        assertEquals(0, urlRepository.count());
    }

    @Test
    // AC-3.4
    void shouldEnforceMinuteQuotaForConcurrentRequests() throws Exception {
        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");
        var requests = new ArrayList<Callable<Integer>>();
        for (int i = 0; i < 20; i++) {
            requests.add(() -> given()
                    .auth().basic(USERNAME, PASSWORD)
                    .header("X-Forwarded-For", "192.0.2.14")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .post("/url")
                    .statusCode());
        }

        var executor = Executors.newFixedThreadPool(10);
        try {
            var responses = executor.invokeAll(requests);
            long successfulRequests = 0;
            for (var response : responses) {
                int status = response.get();
                assertTrue(status == Status.CREATED.getStatusCode()
                        || status == Status.TOO_MANY_REQUESTS.getStatusCode());
                if (status == Status.CREATED.getStatusCode()) {
                    successfulRequests++;
                }
            }

            assertEquals(5, successfulRequests);
            assertEquals(5, urlRepository.count());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    // AC-3.8
    void shouldKeepQuotasIndependentByForwardedIp() {
        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");

        for (int i = 0; i < 5; i++) {
            given()
                    .auth().basic(USERNAME, PASSWORD)
                    .header("X-Forwarded-For", "192.0.2.20")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .post("/url")
                    .then()
                    .statusCode(Status.CREATED.getStatusCode());
        }

        given()
                .auth().basic(USERNAME, PASSWORD)
                .header("X-Forwarded-For", "192.0.2.21")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.CREATED.getStatusCode());
    }

    @Test
    // AC-3.9
    void shouldCreateRateLimitIndexesAtStartup() {
        Document ttlIndex = null;

        for (Document index : rateLimitRepository.mongoCollection().listIndexes()) {
            if ("createdAt_ttl".equals(index.getString("name"))) {
                ttlIndex = index;
            }
        }

        assertNotNull(ttlIndex);
        assertEquals(86400L, ((Number) ttlIndex.get("expireAfterSeconds")).longValue());
        assertEquals(1, ttlIndex.get("key", Document.class).get("createdAt"));
    }

    @Test
    // AC-3.12
    void shouldRejectMissingCredentialsWithoutCreatingLinkOrReservation() {
        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");

        given()
                .header("X-Forwarded-For", "192.0.2.30")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.UNAUTHORIZED.getStatusCode());

        assertEquals(0, urlRepository.count());
        assertEquals(0, rateLimitRepository.count());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"reader"})
    // AC-3.12
    void shouldRejectPrincipalWithoutUserRoleWithoutCreatingLinkOrReservation() {
        var dto = new UrlDto();
        dto.setFullUrl("https://www.usercoding.dev");

        given()
                .header("X-Forwarded-For", "192.0.2.31")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .post("/url")
                .then()
                .statusCode(Status.FORBIDDEN.getStatusCode());

        assertEquals(0, urlRepository.count());
        assertEquals(0, rateLimitRepository.count());
    }
}
