package dev.solocoding.controller;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import dev.solocoding.service.UrlService;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class RedirectController {

    private final UrlService urlService;
    private final String home;


    public RedirectController(UrlService urlService, 
        @ConfigProperty(name = "shorterurl.config.home") String home) {
        this.urlService = urlService;
        this.home = home;
    }    

    @GET
    @Path("/redirect/{shortUrl}")
    public Response redirect(@PathParam String shortUrl) {
        URI uri = URI.create(urlService.getAndRedirect(shortUrl).getFullUrl());
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    public Response redirectToHome() {
        URI uri = URI.create(home);
        return Response.temporaryRedirect(uri).build();
    }


}
