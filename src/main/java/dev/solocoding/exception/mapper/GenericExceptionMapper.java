package dev.solocoding.exception.mapper;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import dev.solocoding.exception.ServiceExceptionEnum;
import dev.solocoding.exception.error.ApiError;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        log.error("Generic exception: {}", exception);
        return Response.status(ServiceExceptionEnum.INTERNAL_ERROR.getHttpStatus())
                        .entity(new ApiError(ServiceExceptionEnum.INTERNAL_ERROR.getCode(), ServiceExceptionEnum.INTERNAL_ERROR.name(), ServiceExceptionEnum.INTERNAL_ERROR.getDescription()))
                        .build();
    }
}
