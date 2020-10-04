package dev.solocoding.exception;

public class NotValidUrlException extends RuntimeException{
    
    private static final long serialVersionUID = 1L;

    public NotValidUrlException() {
        super("Url is not valid");
    }

}
