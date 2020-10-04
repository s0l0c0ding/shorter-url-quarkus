package dev.usercoding.repository.impl;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import dev.usercoding.entity.Url;
import dev.usercoding.repository.UrlRepository;

@ApplicationScoped
public class UrlRepositoryImpl implements UrlRepository {

    @Override
    public Optional<Url> findByShortUrl(String shortUrl) {
        return Optional.ofNullable(find("shortUrl", shortUrl).firstResult());
    }
    
}
