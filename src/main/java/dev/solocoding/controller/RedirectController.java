package dev.solocoding.controller;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import dev.solocoding.service.UrlService;
import lombok.RequiredArgsConstructor;

@Path("/redirect")
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    @GET
    @Path("{shortUrl}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response redirect(@PathParam String shortUrl) {
        URI uri = URI.create(urlService.getAndRedirect(shortUrl).getFullUrl());
        return Response.temporaryRedirect(uri).build();
    }
}