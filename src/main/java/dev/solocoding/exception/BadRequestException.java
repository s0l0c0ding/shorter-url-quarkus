package dev.solocoding.exception;

public class BadRequestException extends RuntimeException {
    
    public BadRequestException(BadRequestEnum desc) {
        super(desc.getDescription());
    }

    public BadRequestException(String desc) {
        super(desc);
    }
}
