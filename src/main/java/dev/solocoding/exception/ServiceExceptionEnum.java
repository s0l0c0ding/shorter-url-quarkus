package dev.solocoding.exception;

import javax.ws.rs.core.Response.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceExceptionEnum {

    NOT_VALID_URL(1, Status.BAD_REQUEST, "Url is not valid"),
    EXPIRED_URL(2, Status.BAD_REQUEST, "Url is expired, will be deleted after 30 days from Expiration: "),
    EXPIRED_DELETED(3, Status.BAD_REQUEST, "Url is expired from 60 days and has been deleted"),
    URL_NOT_FOUND(4, Status.NOT_FOUND, "No such url is present"),
    INTERNAL_ERROR(5, Status.INTERNAL_SERVER_ERROR, "Something bad happend");
    
    private int code;
    private Status httpStatus;
    private String description;
    
}
