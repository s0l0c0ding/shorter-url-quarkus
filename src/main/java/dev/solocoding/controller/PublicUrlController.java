package dev.solocoding.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.components.url.service.UrlService;
import dev.solocoding.exception.error.ApiError;
import lombok.RequiredArgsConstructor;

@Path("/public")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Public URL Controller", description = "Anonymous visitor URL shortening")
@RequiredArgsConstructor
public class PublicUrlController {

    private static final String ERROR_1 = """
            {"code":1,"name":"NOT_VALID_URL","description":"Url is not valid"}
            """;
    private static final String ERROR_5 = """
            {"code":5,"name":"INTERNAL_ERROR","description":"Something bad happend"}
            """;
    private static final String ERROR_7 = """
            {"code":7,"name":"RATE_LIMITED","description":"Too many requests, try again later"}
            """;
    private static final String ERROR_8 = """
            {"code":8,"name":"RATE_LIMIT_UNAVAILABLE","description":"Rate limit store is unavailable"}
            """;

    private final UrlService urlService;

    @POST
    @Path("/url")
    @Operation(summary = "Create a public short URL", description = "Create a 7-day short URL for an anonymous visitor")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UrlDto.class))),
            @APIResponse(responseCode = "400", description = "Invalid URL", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "NOT_VALID_URL", value = ERROR_1))),
            @APIResponse(responseCode = "401", description = "Not Authorized"),
            @APIResponse(responseCode = "403", description = "Not Allowed"),
            @APIResponse(responseCode = "429", description = "Rate limited", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "RATE_LIMITED", value = ERROR_7))),
            @APIResponse(responseCode = "503", description = "Rate limit store unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "RATE_LIMIT_UNAVAILABLE", value = ERROR_8))),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public Response savePublicUrl(UrlDto dto) {
        return Response.status(Response.Status.CREATED)
                .entity(urlService.savePublicUrl(dto))
                .build();
    }
}
