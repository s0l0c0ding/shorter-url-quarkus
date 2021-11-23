package dev.usercoding.exception.error;

import javax.ws.rs.core.Response;

public interface ApiErrorContract {
   
    Response toResponseEntity();
}
