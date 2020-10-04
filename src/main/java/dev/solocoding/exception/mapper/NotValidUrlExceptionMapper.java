package dev.solocoding.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import dev.solocoding.exception.NotValidUrlException;

@Provider
public class NotValidUrlExceptionMapper implements ExceptionMapper<NotValidUrlException> {

    @Override
    public Response toResponse(NotValidUrlException exception) {
        return Response
        .status(Status.BAD_REQUEST)
        .entity(exception.getMessage())
        .build();
    }
    
}
