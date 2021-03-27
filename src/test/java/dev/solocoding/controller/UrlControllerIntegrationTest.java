package dev.solocoding.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import dev.solocoding.common.CountryCount;
import dev.solocoding.conftest.MongoDbContainer;
import dev.solocoding.dto.UrlDto;
import dev.solocoding.entity.Url;
import dev.solocoding.repository.UrlRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

import org.junit.jupiter.api.Test;



@QuarkusTest
@TestSecurity(user = "testUser", roles = {"admin"})
@TestHTTPEndpoint(UrlController.class)
@QuarkusTestResource(MongoDbContainer.class)
class UrlControllerIntegrationTest {
    
    private final static String SHORT_URL = "6GGfP2";

    @Inject
    UrlRepository repo;

    private Url getShortUrl() {
        Url url = new Url();
        url.setId(new ObjectId("5f6b3a94684b1858ec6b33f0"));
        url.setFullUrl("https://www.solocoding.dev");
        url.setShortUrl(SHORT_URL);
        url.setCountryCountList(List.of(new CountryCount("US",1)));
        url.setCount(0);
        url.setVersion(0L);
        return url;
    }


    @BeforeEach
    void init () {
        repo.persist(getShortUrl());
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
        with()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .post()
            .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .log()
            .body()
            .body("fullUrl", is(dto.getFullUrl()));
    }

    @Test
    void saveUrlShouldFailWiht400() {
        var dto = new UrlDto();
        dto.setFullUrl("http//solocoding");
        with()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .post()
            .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode());
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
    void updateUrlByShortIdShouldFailWith400() {
        var dto = new UrlDto();
        dto.setFullUrl("quarkus.io");
        with()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON)
            .put("/{shortUrl}", SHORT_URL)
            .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void deleteUrlByIdShouldSucess() {
        with()
            .delete("/{id}", getShortUrl().getId().toHexString())
            .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());
        assertEquals(0, repo.count());
    }

    @Test
    void deleteUrlByIdShoulFailWith404() {
        with()
            .delete("/{id}","6f6b3a94684b1858ec6b33f1")
            .then()
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }
}
