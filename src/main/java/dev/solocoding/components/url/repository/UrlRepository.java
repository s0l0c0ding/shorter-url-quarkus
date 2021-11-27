package dev.usercoding.components.url.repository;

import java.util.Optional;

import dev.usercoding.components.url.entity.Url;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

public interface UrlRepository extends PanacheMongoRepository<Url> {
    
    Optional<Url> findByShortUrl(String shortUrl);
}
