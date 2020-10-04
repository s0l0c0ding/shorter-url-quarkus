package dev.solocoding.entity;

import org.bson.types.ObjectId;

import dev.solocoding.dto.UrlDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Url {

    private ObjectId id;
    private String fullUrl;
    private String shortUrl;
    private long count;
    private Long version;

    public Url (UrlDto url) {
        id =  url.getId() != null?  new ObjectId(url.getId()) : null;
        fullUrl = url.getFullUrl();
        shortUrl = url.getShortUrl();
        count = url.getCount();
    }

}
