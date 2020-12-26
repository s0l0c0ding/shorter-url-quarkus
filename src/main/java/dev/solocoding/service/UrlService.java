package dev.solocoding.service;

import dev.solocoding.dto.UrlDto;

public interface UrlService {
    
    UrlDto getUrlByShortUrl (String shortUrl);

    /**
     * @param dto
     * Save a new Url into the database with the shot url equal to ObjectId.substring(18, 24)
     * @return UrlDto
     */
    UrlDto saveUrl (UrlDto dto);

    void deleteUrlById (String id);

    /**
     * Return the full url to the controller to redirect, after updating the count attribute and the countCountList of the Url entity
     * @param shortUrl
     * @return UrlDto
     */
    UrlDto getAndRedirect (String shortUrl);
}
