package dev.solocoding.components.request.service;

import javax.enterprise.context.ApplicationScoped;

import io.vertx.core.http.HttpServerRequest;

@ApplicationScoped
public class RequestDetailsImpl implements RequestDetails {

    private HttpServerRequest request;
    private String callerIp;

    @Override
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
