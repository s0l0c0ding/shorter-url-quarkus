package dev.solocoding.components.request.service;

import io.vertx.core.http.HttpServerRequest;

public interface RequestDetails {
    
    String getRemoteAddress();

    HttpServerRequest getHttpServerRequest();

    void setRequest(HttpServerRequest request, String callerIp);
}
