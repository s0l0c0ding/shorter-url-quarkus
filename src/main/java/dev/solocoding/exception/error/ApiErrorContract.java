package dev.solocoding.exception.error;

import jakarta.ws.rs.core.Response;

public interface ApiErrorContract {
   
    Response toResponseEntity();
}
