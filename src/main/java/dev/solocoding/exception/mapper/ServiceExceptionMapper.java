package dev.usercoding.exception.mapper;

import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.ext.ExceptionMapper;

import jakarta.ws.rs.ext.Provider;

import dev.usercoding.exception.ServiceException;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

    @Override
    public Response toResponse(ServiceException exception) {
        return exception.toResponseEntity();
    }
    
}
