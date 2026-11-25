package dev.solocoding.controller;

import static dev.solocoding.common.Constants.PATH_PARAM_SHORT_URL;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.DELETE;

import jakarta.ws.rs.GET;

import jakarta.ws.rs.PATCH;

import jakarta.ws.rs.POST;

import jakarta.ws.rs.PUT;

import jakarta.ws.rs.Path;

import jakarta.ws.rs.Produces;

import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.components.url.service.UrlService;
import dev.solocoding.exception.error.ApiError;
import lombok.RequiredArgsConstructor;

@Path("/url")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Url Controller", description = "Create, read, update, and delete shortened URLs")
@RequiredArgsConstructor
public class UrlController {

    private static final String ERROR_1 = "{\"code\":1,\"name\":\"NOT_VALID_URL\",\"description\":\"Url is not valid\"}";
    private static final String ERROR_2 = "{\"code\":2,\"name\":\"EXPIRED_URL\",\"description\":\"Url is expired\"}";
    private static final String ERROR_3 = "{\"code\":3,\"name\":\"EXPIRED_DELETED\",\"description\":\"Url is expired for more than 30 days and has been deleted\"}";
    private static final String ERROR_4 = "{\"code\":4,\"name\":\"URL_NOT_FOUND\",\"description\":\"No such url is present\"}";
    private static final String ERROR_5 = "{\"code\":5,\"name\":\"INTERNAL_ERROR\",\"description\":\"Something bad happend\"}";
    private static final String ERROR_6 = "{\"code\":6,\"name\":\"NOT_VALID_EXPIRATION\",\"description\":\"Expiration time must be between 1 and 365 days from now\"}";

    private final UrlService urlService;

    @GET
    @Path("/{shortUrl}")
    @Operation(summary = "Get URL details by short URL", description = "Return the URL identified by the short URL key")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UrlDto.class))),
            @APIResponse(responseCode = "400", description = "Expired URL", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = {
                    @ExampleObject(name = "EXPIRED_URL", value = ERROR_2),
                    @ExampleObject(name = "EXPIRED_DELETED", value = ERROR_3) })),
            @APIResponse(responseCode = "401", description = "Not Authorized"),
            @APIResponse(responseCode = "403", description = "Not Allowed"),
            @APIResponse(responseCode = "404", description = "URL not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "URL_NOT_FOUND", value = ERROR_4))),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public UrlDto getUrlByShortUrl(@Parameter(name = PATH_PARAM_SHORT_URL, in = ParameterIn.PATH, required = true, description = "Short URL key", schema = @Schema(type = SchemaType.STRING)) @PathParam(PATH_PARAM_SHORT_URL) String shortUrl) {
        return urlService.getUrlByShortUrl(shortUrl);
    }

    @GET
    @RolesAllowed("admin")
    @Operation(summary = "List URLs", description = "Return a page of shortened URLs")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UrlDto.class, type = SchemaType.ARRAY))),
            @APIResponse(responseCode = "401", description = "Not Authorized"),
            @APIResponse(responseCode = "403", description = "Not Allowed"),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public List<UrlDto> getAll(
            @Parameter(name = "index", in = ParameterIn.QUERY, description = "0-based page number", schema = @Schema(type = SchemaType.INTEGER, format = "int32")) @QueryParam("index") int index,
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = SchemaType.INTEGER, format = "int32")) @QueryParam("size") int size) {
        return urlService.getAll(index, size);
    }

    @POST
    @Operation(summary = "Save URL", description = "Make a new short URL by providing the full URL in the payload")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "CREATED", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UrlDto.class))),
            @APIResponse(responseCode = "400", description = "Invalid URL or expiration", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = {
                    @ExampleObject(name = "NOT_VALID_URL", value = ERROR_1),
                    @ExampleObject(name = "NOT_VALID_EXPIRATION", value = ERROR_6) })),
            @APIResponse(responseCode = "401", description = "Not Authorized"),
            @APIResponse(responseCode = "403", description = "Not Allowed"),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public Response saveUrl(UrlDto dto) {
        return Response.status(Status.CREATED).entity(urlService.saveUrl(dto)).build();
    }

    @PUT
    @Path("/{shortUrl}")
    @Operation(summary = "Update URL", description = "Update the full URL for a given short URL")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UrlDto.class))),
            @APIResponse(responseCode = "400", description = "Invalid or expired URL", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = {
                    @ExampleObject(name = "NOT_VALID_URL", value = ERROR_1),
                    @ExampleObject(name = "EXPIRED_URL", value = ERROR_2),
                    @ExampleObject(name = "EXPIRED_DELETED", value = ERROR_3) })),
            @APIResponse(responseCode = "401", description = "Not Authorized"),
            @APIResponse(responseCode = "403", description = "Not Allowed"),
            @APIResponse(responseCode = "404", description = "URL not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "URL_NOT_FOUND", value = ERROR_4))),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public UrlDto updateUrlByShortId(@Parameter(name = PATH_PARAM_SHORT_URL, in = ParameterIn.PATH, required = true, description = "Short URL key", schema = @Schema(type = SchemaType.STRING)) @PathParam(PATH_PARAM_SHORT_URL) String shortUrl, UrlDto dto) {
        return urlService.updateUrlByShortId(shortUrl, dto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Operation(summary = "Delete URL", description = "Delete the URL with the given id")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "No Content"),
            @APIResponse(responseCode = "401", description = "Not Authorized"),
            @APIResponse(responseCode = "403", description = "Not Allowed"),
            @APIResponse(responseCode = "404", description = "URL not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "URL_NOT_FOUND", value = ERROR_4))),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public void deleteUrlById(@Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "URL id", schema = @Schema(type = SchemaType.STRING)) @PathParam("id") String id) {
        urlService.deleteUrlById(id);
    }

    @PATCH
    @Path("/{shortUrl}/extend")
    @Operation(summary = "Extend expiration", description = "Extend the expiration of a short URL by 30 days")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UrlDto.class))),
            @APIResponse(responseCode = "401", description = "Not Authorized"),
            @APIResponse(responseCode = "403", description = "Not Allowed"),
            @APIResponse(responseCode = "404", description = "URL not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "URL_NOT_FOUND", value = ERROR_4))),
            @APIResponse(responseCode = "500", description = "Internal error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiError.class), examples = @ExampleObject(name = "INTERNAL_ERROR", value = ERROR_5)))
    })
    public UrlDto extendExpirationByOnMonth(@Parameter(name = PATH_PARAM_SHORT_URL, in = ParameterIn.PATH, required = true, description = "Short URL key", schema = @Schema(type = SchemaType.STRING)) @PathParam(PATH_PARAM_SHORT_URL) String shortUrl) {
        return urlService.extendExpiration(shortUrl, 30);
    }

}
