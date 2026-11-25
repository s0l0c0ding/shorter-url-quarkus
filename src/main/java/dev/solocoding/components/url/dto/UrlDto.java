package dev.solocoding.components.url.dto;

import java.time.ZonedDateTime;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import dev.solocoding.common.CountryCount;
import dev.solocoding.components.url.entity.Url;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "UrlDto", description = "Shortened URL details")
public class UrlDto {

    @Schema(description = "Id of the URL", examples = {"672b1f8c9a1b2c3d4e5f6a7b"})
    private String id;
    @Schema(description = "Original URL", examples = {"https://solocoding.dev/"})
    private String fullUrl;
    @Schema(description = "Short URL key", examples = {"abc123"})
    private String shortUrl;
    @Schema(description = "Per-country redirect counts")
    private List<CountryCount> countryCountList;
    @Schema(description = "Total redirect count", format = "int64")
    private long count;
    @Schema(description = "Expiration time", format = "date-time")
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
