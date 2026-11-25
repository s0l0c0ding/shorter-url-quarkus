package dev.solocoding.components.url.service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Pattern;

import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.exception.ServiceException;
import dev.solocoding.exception.ServiceExceptionEnum;

public interface UrlValidation {

    long MIN_EXPIRE_DAYS = 1L;
    long MAX_EXPIRE_DAYS = 365L;
    long DEFAULT_EXPIRE_DAYS = 30L;
    Duration EXPIRE_TIME_TOLERANCE = Duration.ofMinutes(1);
    
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

    /**
     * A requested expiration must stay between 1 and 365 days from now.
     * An absent value is allowed and resolved by the caller to the default.
     * @param expireTime
     */
    default void isValidExpireTime(ZonedDateTime expireTime) {
        if(Objects.isNull(expireTime)) return;
        final var now = ZonedDateTime.now(ZoneOffset.UTC);
        final var earliest = now.plusDays(MIN_EXPIRE_DAYS).minus(EXPIRE_TIME_TOLERANCE);
        final var latest = now.plusDays(MAX_EXPIRE_DAYS).plus(EXPIRE_TIME_TOLERANCE);
        if(expireTime.isBefore(earliest) || expireTime.isAfter(latest))
                throw new ServiceException(ServiceExceptionEnum.NOT_VALID_EXPIRATION, "");
    }

}
