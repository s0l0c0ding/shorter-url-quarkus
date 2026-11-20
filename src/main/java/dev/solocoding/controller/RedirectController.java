package dev.solocoding.controller;

import static dev.solocoding.common.Constants.PATH_PARAM_SHORT_URL;

import java.net.URI;

import jakarta.annotation.security.PermitAll;

import jakarta.ws.rs.GET;

import jakarta.ws.rs.Path;

import jakarta.ws.rs.Produces;

import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import dev.solocoding.components.url.service.UrlService;
import dev.solocoding.exception.error.ApiError;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@Tag(name = "Redirect Controller", description = "Public redirects")
public class RedirectController {

    private static final String ERROR_2 = "{\"code\":2,\"name\":\"EXPIRED_URL\",\"description\":\"Url is expired\"}";
    private static final String ERROR_3 = "{\"code\":3,\"name\":\"EXPIRED_DELETED\",\"description\":\"Url is expired from 60 days and has been deleted\"}";
    private static final String ERROR_4 = "{\"code\":4,\"name\":\"URL_NOT_FOUND\",\"description\":\"No such url is present\"}";
    private static final String ERROR_5 = "{\"code\":5,\"name\":\"INTERNAL_ERROR\",\"description\":\"Something bad happend\"}";

    private final UrlService urlService;
    private final String home;


    public RedirectController(UrlService urlService, 
        @ConfigProperty(name = "shorterurl.config.home") String home) {
        this.urlService = urlService;
        this.home = home;
    }    

    @GET
    @PermitAll
    @Path("/redirect/{shortUrl}")
    @Operation(summary = "Redirect", description = "Redirect the end user to the full URL")
    @APIResponses({
            @APIResponse(responseCode = "307", description = "Temporary Redirect", headers = @Header(name = "Location", description = "Target URL", schema = @Schema(type = SchemaType.STRING, format = "uri"))),
            @APIResponse(responseCode = "400", description = "Expired URL", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = {
                    @ExampleObject(name = "EXPIRED_URL", value = ERROR_2),
                    @ExampleObject(name = "EXPIRED_DELETED", value = ERROR_3) })),
            @APIResponse(responseCode = "404", description = "URL not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "URL_NOT_FOUND", value = ERROR_4))),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public Response redirect(@Parameter(name = PATH_PARAM_SHORT_URL, in = ParameterIn.PATH, required = true, description = "Short URL key", schema = @Schema(type = SchemaType.STRING)) @PathParam(PATH_PARAM_SHORT_URL) String shortUrl) {
        URI uri = URI.create(urlService.getAndRedirect(shortUrl).getFullUrl());
        return Response.temporaryRedirect(uri).build();
    }

    @GET
    @PermitAll
    @Operation(summary = "Redirect to home", description = "Redirect the end user to the configured home page")
    @APIResponse(responseCode = "307", description = "Temporary Redirect", headers = @Header(name = "Location", description = "Target URL", schema = @Schema(type = SchemaType.STRING, format = "uri")))
    public Response redirectToHome() {
        URI uri = URI.create(home);
        return Response.temporaryRedirect(uri).build();
    }

}
