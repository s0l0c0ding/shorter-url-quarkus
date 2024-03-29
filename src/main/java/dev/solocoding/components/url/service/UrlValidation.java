package dev.solocoding.components.url.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Pattern;

import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.exception.ServiceException;
import dev.solocoding.exception.ServiceExceptionEnum;

public interface UrlValidation {
    
    default void isValidUrl(UrlDto dto) {
        final var regex = "((http|https)://)(www.)?" + "[a-zA-Z0-9@:%._\\+~#?&/=]" + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%" + "._\\+~#?&/=]*)";
        if(! (Objects.nonNull(dto) && Objects.nonNull(dto.getFullUrl()) && dto.getFullUrl().length() <= 350 && Pattern.matches(regex, dto.getFullUrl())) ) 
                throw new ServiceException(ServiceExceptionEnum.NOT_VALID_URL, "");
    }

    default boolean isEXpireTimeAfterDays(ZonedDateTime dateTime, Long days) {
        if(Objects.isNull(dateTime)) return false;
        final var now = ZonedDateTime.now(ZoneOffset.UTC);
        return dateTime.until(now.plusDays(1), ChronoUnit.DAYS) >= days ;
    }

}
