package dev.solocoding.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import dev.solocoding.common.CountryCount;
import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.components.url.entity.Url;
import dev.solocoding.components.url.repository.UrlRepository;
import dev.solocoding.conftest.MongoDbContainer;
import dev.solocoding.exception.ServiceExceptionEnum;
import dev.solocoding.exception.error.ApiError;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

import org.junit.jupiter.api.Test;



@QuarkusTest
@TestSecurity(user = "testUser", roles = {"user", "admin"})
@TestHTTPEndpoint(UrlController.class)
@QuarkusTestResource(MongoDbContainer.class)
class UrlControllerIntegrationTest {
    
    private final static String SHORT_URL = "6GGfP2";
    private final static String SHORT_URL_EXPIRED = "EXPIRED";

    @Inject
    UrlRepository repo;

    private Url getShortUrl() {
        Url url = new Url();
        url.setId(new ObjectId("5f6b3a94684b1858ec6b33f0"));
        url.setFullUrl("https://www.solocoding.dev");
        url.setShortUrl(SHORT_URL);
        url.setCountryCountList(List.of(new CountryCount("US",1)));
        url.setCount(0);
        url.setExpireTime(ZonedDateTime.now(ZoneOffset.UTC).plusDays(10));
        url.setVersion(0L);
        return url;
    }

    private Url getExpiredShortUrl() {
        Url url = new Url();
        url.setId(new ObjectId());
        url.setFullUrl("https://www.expired.dev");
        url.setShortUrl(SHORT_URL_EXPIRED);
        url.setCountryCountList(List.of(new CountryCount("US",1)));
        url.setCount(0);
        url.setExpireTime(ZonedDateTime.now(ZoneOffset.UTC).minusDays(65));
        url.setVersion(0L);
        return url;
    }


    @BeforeEach
    void init () {
        repo.persist(getShortUrl(), getExpiredShortUrl());
    }

    @AfterEach
    void clean () {
        repo.deleteAll();
    }

    @Test
    void getUrlByShortUrlShouldSucess() {
        given()
            .get("/{shortUrl}", SHORT_URL)
            .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .log()
            .body()
            .body("id", is(getShortUrl().getId().toHexString()))
            .body("fullUrl", is(getShortUrl().getFullUrl()));
    }


    @Test
    void getUrlByShortUrlShouldFailWith400WhenUrlIsExpired() {
        var actual = with()
            .get("/{shortUrl}", SHORT_URL_EXPIRED)
            .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON).extract().as(ApiError.class);
        assertEquals(1, repo.count());
        assertEquals(ServiceExceptionEnum.EXPIRED_DELETED.getCode(), actual.getCode());
        assertEquals(ServiceExceptionEnum.EXPIRED_DELETED.getDescription(), actual.getDescription());
    }

    @Test
    void getAllShouldSucess() {
        given()
            .get(URI.create("?index=0&size=10"))
            .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .log()
            .body()
            .body("[0].id", is(getShortUrl().getId().toHexString()))
            .body("[0].fullUrl", is(getShortUrl().getFullUrl()));
    }

    @Test
    void saveUrlShouldSucess() {
        var dto = new UrlDto();
        dto.setFullUrl("https://solocoding.dev");
        var actual = with()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .post()
            .then()
            .statusCode(Status.CREATED.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON).extract().as(UrlDto.class);
        
        assertEquals(dto.getFullUrl(), actual.getFullUrl());
        assertNotNull(actual.getExpireTime());
    }

    @Test
    void saveUrlShouldFailWiht400WhenUrlIsInvalid() {
        var dto = new UrlDto();
        dto.setFullUrl("http//solocoding");
        var actual = with()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .post()
            .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON).extract().as(ApiError.class);
        assertEquals(ServiceExceptionEnum.NOT_VALID_URL.getCode(), actual.getCode());
        assertEquals(ServiceExceptionEnum.NOT_VALID_URL.getDescription(), actual.getDescription());
    }

    @Test
    void updateUrlByShortIdShouldSucess() {
        var dto = new UrlDto();
        dto.setFullUrl("https://quarkus.io");
        with()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .put("/{shortUrl}", SHORT_URL)
            .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .log()
            .body()
            .body("fullUrl", is(dto.getFullUrl()));
    }


    @Test
    void updateUrlByShortIdShouldFailWith400WhenUrlIsInvalid() {
        var dto = new UrlDto();
        dto.setFullUrl("quarkus.io");
        var actual = with()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .put("/{shortUrl}", SHORT_URL)
            .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON).extract().as(ApiError.class);
        assertEquals(ServiceExceptionEnum.NOT_VALID_URL.getCode(), actual.getCode());
        assertEquals(ServiceExceptionEnum.NOT_VALID_URL.getDescription(), actual.getDescription());;
    }

    @Test
    void deleteUrlByIdShouldSucess() {
        with()
            .delete("/{id}", getShortUrl().getId().toHexString())
            .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());
        assertEquals(1, repo.count());
    }

    @Test
    void deleteUrlByIdShoulFailWith404WhenNoUrlIsFound() {
        var actual = with()
            .delete("/{id}","6f6b3a94684b1858ec6b33f1")
            .then()
            .statusCode(Status.NOT_FOUND.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON).extract().as(ApiError.class);
        assertEquals(ServiceExceptionEnum.URL_NOT_FOUND.getCode(), actual.getCode());
        assertEquals(ServiceExceptionEnum.URL_NOT_FOUND.getDescription(), actual.getDescription());
    }

    @Test
    void patchUrlShouldSucess() {
        var actual = with()
            .contentType(MediaType.APPLICATION_JSON)
            .patch("/{id}/extend", getShortUrl().getShortUrl())
            .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON).extract().as(UrlDto.class);
        
        assertNotNull(actual.getExpireTime());
        assertEquals(30l, ZonedDateTime.now(ZoneOffset.UTC).until(actual.getExpireTime().plusDays(1), ChronoUnit.DAYS));
    }
}
