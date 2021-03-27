package dev.solocoding.controller;

import static io.restassured.RestAssured.given;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.solocoding.common.CountryCount;
import dev.solocoding.conftest.MongoDbContainer;
import dev.solocoding.entity.Url;
import dev.solocoding.repository.UrlRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(RedirectController.class)
@QuarkusTestResource(MongoDbContainer.class)
class RedirectControllerIntegrationTest {
    
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
    void whenRedirectShouldSuccess() {
        given().get("/redirect/{shortUrl}", SHORT_URL)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(MediaType.TEXT_HTML);
    }


    @Test
    void whenPathIsBlankRedirectShouldSuccess() {
        given().get()
                .then()
                .statusCode(Status.OK.getStatusCode())
                .contentType(MediaType.TEXT_HTML);
    }
}
