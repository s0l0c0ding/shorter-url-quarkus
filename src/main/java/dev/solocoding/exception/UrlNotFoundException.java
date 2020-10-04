package dev.solocoding.exception;

public class UrlNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 9071167316764573188L;

    public UrlNotFoundException() {
        super("No such url is present");
    }

}
