package dev.solocoding.controller;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import dev.solocoding.dto.UrlDto;
import dev.solocoding.service.UrlService;
import lombok.RequiredArgsConstructor;

@Path("/url")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UrlController {
   
    private final UrlService urlService;

    @GET
    @Path("/{shortUrl}")
    public UrlDto getUrlByShortUrl(@PathParam String shortUrl) {
        return urlService.getUrlByShortUrl(shortUrl);
    }

    @GET
    public List<UrlDto> getAll(@QueryParam int index, @QueryParam int size) {
        return urlService.getAll(index, size);
    }

    @POST
    public UrlDto saveUrl(UrlDto dto) {
        return urlService.saveUrl(dto);
    }

    @PUT
    @Path("/{shortUrl}")
    public UrlDto updateUrlByShortId(@PathParam String shortUrl, UrlDto dto) {
        return urlService.updateUrlByShortId(shortUrl, dto);
    }

    @DELETE
    @Path("/{id}")
    public void deleteUrlById(@PathParam String id) {
        urlService.deleteUrlById(id);
    }

    @PATCH
    @Path("/{shortUrl}/extend")
    public UrlDto extendExpirationByOnMonth(@PathParam String shortUrl) {
        return urlService.extendExpiration(shortUrl, 30);
    }

}
