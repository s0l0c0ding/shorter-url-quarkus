package dev.solocoding.service.impl;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;

import dev.solocoding.dto.UrlDto;
import dev.solocoding.entity.Url;
import dev.solocoding.exception.NotValidUrlException;
import dev.solocoding.exception.UrlNotFoundException;
import dev.solocoding.repository.UrlRepository;
import dev.solocoding.service.UrlService;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository repo;

    @Override
    public UrlDto getUrlByShortUrl(String shortUrl) {
        return new UrlDto(checkExistence(shortUrl));
    }

    @Override
    public UrlDto saveUrl(UrlDto dto) {
        if (!isValidUrl(dto)) {
            throw new NotValidUrlException();
        }
        var entity = new Url(dto);
        if (entity.getId() == null) {
            ObjectId id = new ObjectId();
            entity.setId(id);
            entity.setShortUrl(id.toHexString().substring(18, 24));
        }
        repo.persist(entity);
        return new UrlDto(entity);
    }

    @Override
    public void deleteUrlById(String id) {
        if (ObjectId.isValid(id)) {
            Optional<Url> url = Optional.ofNullable(repo.findById(new ObjectId(id)));
            if(url.isEmpty()) {
               throw new UrlNotFoundException();
            } else {
                repo.deleteById(url.get().getId());
            }     
        }
    }

    @Override
    public UrlDto getAndRedirect(String shortUrl) {
        Url url = checkExistence(shortUrl);
        url.setCount(url.getCount() + 1);
        repo.update(url);
        return new UrlDto(url);
    }

    protected Url checkExistence(String shortUrl) {
        return repo.findByShortUrl(shortUrl).orElseThrow(() -> new UrlNotFoundException());
    }

    protected boolean isValidUrl(UrlDto url) {
        String regex = "((http|https)://)(www.)?" + "[a-zA-Z0-9@:%._\\+~#?&/=]" + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%" + "._\\+~#?&/=]*)";
        return url.getFullUrl() != null && Pattern.matches(regex, url.getFullUrl());
    }

}