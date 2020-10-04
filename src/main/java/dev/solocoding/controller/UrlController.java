package dev.solocoding.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import dev.solocoding.dto.UrlDto;
import dev.solocoding.service.UrlService;
import lombok.RequiredArgsConstructor;

@Path("/url")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UrlController {
   
    private final UrlService urlService;

    @GET
    @Path("/{shortUrl}")
    public  UrlDto getUrlByShortUrl(@PathParam String shortUrl) {
        return urlService.getUrlByShortUrl(shortUrl);
    }

    @POST
    public  UrlDto saveUrl(UrlDto dto) {
        return urlService.saveUrl(dto);
    }

    @DELETE
    @Path("/{id}")
    public void deleteUrlById(@PathParam String id) {
        urlService.deleteUrlById(id);
    }

}
