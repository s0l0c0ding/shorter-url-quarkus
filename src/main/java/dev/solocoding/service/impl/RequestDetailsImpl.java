package dev.solocoding.service.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

import dev.solocoding.service.RequestDetails;
import io.vertx.core.http.HttpServerRequest;

@RequestScoped
public class RequestDetailsImpl implements RequestDetails {

    private HttpServerRequest request;
    private String callerIp;

    @Override
    @PostConstruct
    public void setRequest(HttpServerRequest request, String callerIp) {
        this.request = request;
        this.callerIp = callerIp;
    }

    @Override
    public String getRemoteAddress() {
        return callerIp;
    }

    @Override
    public HttpServerRequest getHttpServerRequest() {
        return request;
    }
}
