package dev.solocoding.service.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

import dev.solocoding.service.RequestDetails;
import io.vertx.core.http.HttpServerRequest;

@RequestScoped
public class RequestDetailsImpl implements RequestDetails {

    HttpServerRequest request;

    @Override
    @PostConstruct
    public void setRequest(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public String getRemoteAddress() {
        return request.remoteAddress().toString();
    }

    @Override
    public HttpServerRequest getHttpServerRequest() {
        return request;
    }
}
