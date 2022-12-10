package dev.solocoding.controller;

import static dev.solocoding.common.Constants.PATH_PARAM_SHORT_URL;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.components.url.service.UrlService;
import lombok.RequiredArgsConstructor;

@Path("/url")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UrlController {
   
    private final UrlService urlService;

    @GET
    @Path("/{shortUrl}")
    public UrlDto getUrlByShortUrl(@PathParam(PATH_PARAM_SHORT_URL) String shortUrl) {
        return urlService.getUrlByShortUrl(shortUrl);
    }

    @GET
    @RolesAllowed("admin")
    public List<UrlDto> getAll(@QueryParam("index") int index, @QueryParam("size") int size) {
        return urlService.getAll(index, size);
    }

    @POST
    public Response saveUrl(UrlDto dto) {
        return Response.status(Status.CREATED).entity(urlService.saveUrl(dto)).build();
    }

    @PUT
    @Path("/{shortUrl}")
    public UrlDto updateUrlByShortId(@PathParam(PATH_PARAM_SHORT_URL) String shortUrl, UrlDto dto) {
        return urlService.updateUrlByShortId(shortUrl, dto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public void deleteUrlById(@PathParam("id") String id) {
        urlService.deleteUrlById(id);
    }

    @PATCH
    @Path("/{shortUrl}/extend")
    public UrlDto extendExpirationByOnMonth(@PathParam(PATH_PARAM_SHORT_URL) String shortUrl) {
        return urlService.extendExpiration(shortUrl, 30);
    }

}
