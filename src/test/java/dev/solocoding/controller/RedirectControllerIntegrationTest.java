package dev.solocoding.controller;

import static io.restassured.RestAssured.given;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.solocoding.common.Constants;
import dev.solocoding.components.url.entity.Url;
import dev.solocoding.components.url.repository.UrlRepository;
import dev.solocoding.conftest.MongoDbContainer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;

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
        given().config(RestAssured.config().redirect(RestAssuredConfig.config().getRedirectConfig().followRedirects(false)))
                .get("/redirect/{shortUrl}", SHORT_URL)
                .then()
                .statusCode(Status.TEMPORARY_REDIRECT.getStatusCode());
    }


    @Test
    void whenPathIsBlankRedirectShouldSuccess() {
        given().config(RestAssured.config().redirect(RestAssuredConfig.config().getRedirectConfig().followRedirects(false)))
                .get()
                .then()
                .statusCode(Status.TEMPORARY_REDIRECT.getStatusCode());
    }

    @Test
    void whenRedirectWithMultipleIPShouldSuccess() {
        given().config(RestAssured.config().redirect(RestAssuredConfig.config().getRedirectConfig().followRedirects(false)))
                .header(Constants.FORWARDED_HEADER, "184.73.132.126, 23.23.103.207")
                .get("/redirect/{shortUrl}", SHORT_URL)
                .then()
                .statusCode(Status.TEMPORARY_REDIRECT.getStatusCode());
    }

}
