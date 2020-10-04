package dev.solocoding.repository.impl;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import dev.solocoding.entity.Url;
import dev.solocoding.repository.UrlRepository;

@ApplicationScoped
public class UrlRepositoryImpl implements UrlRepository {

    @Override
    public Optional<Url> findByShortUrl(String shortUrl) {
        return Optional.ofNullable(find("shortUrl", shortUrl).firstResult());
    }
    
}
