package dev.solocoding.dto;

import dev.solocoding.entity.Url;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UrlDto {
    
    private String id;
    private String fullUrl;
    private String shortUrl;
    private long count;

    public UrlDto (Url url) {
        id = url.getId().toHexString();
        fullUrl = url.getFullUrl();
        shortUrl = url.getShortUrl();
        count = url.getCount();
    }
}
