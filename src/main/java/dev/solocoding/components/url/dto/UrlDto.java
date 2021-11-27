package dev.usercoding.components.url.dto;

import java.time.ZonedDateTime;
import java.util.List;

import dev.usercoding.common.CountryCount;
import dev.usercoding.components.url.entity.Url;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UrlDto {

    private String id;
    private String fullUrl;
    private String shortUrl;
    private List<CountryCount> countryCountList;
    private long count;
    private ZonedDateTime expireTime;

    public UrlDto (Url url) {
        id = url.getId().toHexString();
        fullUrl = url.getFullUrl();
        shortUrl = url.getShortUrl();
        countryCountList = url.getCountryCountList();
        count = url.getCount();
        expireTime = url.getExpireTime();
    }
}
