package dev.usercoding.service;

import dev.usercoding.dto.UrlDto;

public interface UrlService {
    
    UrlDto getUrlByShortUrl (String shortUrl);

    UrlDto saveUrl (UrlDto dto);

    void deleteUrlById (String id);

    UrlDto getAndRedirect (String shortUrl);
}
