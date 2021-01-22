package dev.solocoding.exception.mapper;
import static dev.solocoding.common.Constants.INTERNAL_ERROR;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        log.error("Generic exception: {}", exception);
        return Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(INTERNAL_ERROR)
                        .build();
    }
}
