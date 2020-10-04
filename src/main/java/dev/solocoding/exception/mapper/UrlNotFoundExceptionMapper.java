package dev.solocoding.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import dev.solocoding.exception.UrlNotFoundException;

@Provider
public class UrlNotFoundExceptionMapper implements ExceptionMapper<UrlNotFoundException> {

    @Override
    public Response toResponse(UrlNotFoundException exception) {    
        return Response
        .status(Status.NOT_FOUND)
        .entity(exception.getMessage())
        .build();
    }
    
}
