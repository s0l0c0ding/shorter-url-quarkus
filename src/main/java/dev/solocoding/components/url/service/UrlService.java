package dev.solocoding.components.url.service;

import java.util.List;

import dev.solocoding.components.url.dto.UrlDto;

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

    /**
     * Update the full url by the short one
     * @param shortUrl
     * @param dto
     * @return
     */
    UrlDto updateUrlByShortId(String shortUrl, UrlDto dto);

	List<UrlDto> getAll(int index, int size);

   /**
    * Will extend the expiration date by n days from today
    * @param shortUrl
    * @param days
    * @return UrlDto
    */
    UrlDto extendExpiration(String shortUrl, long days);
}
