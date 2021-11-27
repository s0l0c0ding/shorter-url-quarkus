package dev.solocoding.components.url.repository;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import dev.solocoding.components.url.entity.Url;

@ApplicationScoped
public class UrlRepositoryImpl implements UrlRepository {

    @Override
    public Optional<Url> findByShortUrl(String shortUrl) {
        return Optional.ofNullable(find("shortUrl", shortUrl).firstResult());
    }
    
}
