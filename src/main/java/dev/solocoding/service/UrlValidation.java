package dev.solocoding.service;

import java.util.Objects;
import java.util.regex.Pattern;

import dev.solocoding.dto.UrlDto;
import dev.solocoding.exception.NotValidUrlException;

public interface UrlValidation {
    
    default void isValidUrl(UrlDto dto) {
        final var regex = "((http|https)://)(www.)?" + "[a-zA-Z0-9@:%._\\+~#?&/=]" + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%" + "._\\+~#?&/=]*)";
        if(! (Objects.nonNull(dto.getFullUrl()) && dto.getFullUrl().length() <= 350 && Pattern.matches(regex, dto.getFullUrl())) ) 
                throw new NotValidUrlException();
    }
}
