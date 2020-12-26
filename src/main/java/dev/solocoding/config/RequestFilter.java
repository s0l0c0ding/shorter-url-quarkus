package dev.solocoding.config;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import dev.solocoding.service.RequestDetails;
import io.vertx.core.http.HttpServerRequest;

@Provider
public class RequestFilter implements ContainerRequestFilter {

    @Context
    HttpServerRequest request;

    @Inject
    RequestDetails requestDetails;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestDetails.setRequest(request);
    }
    
}
