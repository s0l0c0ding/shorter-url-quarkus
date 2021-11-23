package dev.solocoding.exception;

import javax.ws.rs.core.Response;

import dev.solocoding.exception.error.ApiError;
import dev.solocoding.exception.error.ApiErrorContract;


public class ServiceException extends RuntimeException implements ApiErrorContract {

    private final ServiceExceptionEnum enumServiceException;
    private final String info;
    
    public ServiceException(ServiceExceptionEnum en) {
        enumServiceException = en;
        info ="";
    }

    public ServiceException(ServiceExceptionEnum en, String additionalInfo) {
        enumServiceException = en;
        info = additionalInfo;
    }


    @Override
    public Response toResponseEntity() {
       return Response.status(enumServiceException.getHttpStatus())
                        .entity(new ApiError(enumServiceException.getCode(), enumServiceException.name(), enumServiceException.getDescription().concat(info)))
                        .build();
    }
    
}
