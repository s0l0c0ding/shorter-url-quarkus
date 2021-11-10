package dev.solocoding.entity;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;

import dev.solocoding.common.CountryCount;
import dev.solocoding.dto.UrlDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@BsonDiscriminator
public class Url {

    private ObjectId id;
    private String fullUrl;
    private String shortUrl;
    private List<CountryCount> countryCountList;
    private long count;
    private Long version;
    private ZonedDateTime expireTime;

    public Url (UrlDto url) {
        id =  url.getId() != null?  new ObjectId(url.getId()) : null;
        fullUrl = url.getFullUrl();
        shortUrl = url.getShortUrl();
        countryCountList = url.getCountryCountList();
        count = url.getCount();
        this.expireTime = ZonedDateTime.now(ZoneOffset.UTC).plusDays(30l);
    }

    // public boolean isExpired() {
    //     return this.expireTime.isAfter(ZonedDateTime.now(ZoneOffset.UTC));
    // }

}
