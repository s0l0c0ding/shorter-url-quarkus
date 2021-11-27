package dev.solocoding.config;

import java.io.IOException;
import java.util.function.UnaryOperator;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import dev.solocoding.common.Constants;
import dev.solocoding.components.request.service.RequestDetails;
import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class RequestFilter implements ContainerRequestFilter {

    @Context
    HttpServerRequest request;

    @Inject
    RequestDetails requestDetails;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String headerIp = requestContext.getHeaders().entrySet().stream()
                                                .filter(h -> h.getKey().equalsIgnoreCase(Constants.FORWARDED_HEADER))
                                                .findFirst()
                                                .map(h -> h.getValue().get(0))
                                                .map(getSingleIp)
                                                .orElseGet(() -> request.remoteAddress().toString());
        log.info("Header 'x-forwarded-for': {}", headerIp);
        requestDetails.setRequest(request, headerIp);
    }


    UnaryOperator<String> getSingleIp = ips -> {
        var comma = ",";
        if(ips.contains(comma)) {
            return ips.split(comma)[0];
        }
        return ips;
    };
}
