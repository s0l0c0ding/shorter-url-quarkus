package dev.solocoding.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.solocoding.dto.UrlDto;
import dev.solocoding.entity.Url;
import dev.solocoding.exception.NotValidUrlException;
import dev.solocoding.exception.UrlNotFoundException;
import dev.solocoding.repository.UrlRepository;

class UrlServiceImplTest {
    
    private final static String SHORT_URL = "6GGfP2";
    private UrlServiceImpl urlService;
    private UrlRepository repo;

    private Url getShortUrlStub() {
        Url url = new Url();
        url.setId(new ObjectId("5f6b3a94684b1858ec6b33f0"));
        url.setFullUrl("https://www.solocoding.dev");
        url.setShortUrl(SHORT_URL);
        url.setCount(0);
        url.setVersion(0L);
        return url;
    }

    @BeforeEach
    void setUp() {
        repo = mock(UrlRepository.class);
        urlService = new UrlServiceImpl(repo);
    }

    @Test
    void shoulThrowExceptionWhenNoUrlFound() {
        when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> urlService.getUrlByShortUrl(SHORT_URL));
    }

    @Test
    void shouldGetUrlByShortUrl() {
        when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(getShortUrlStub()));
        UrlDto expected = new UrlDto(getShortUrlStub());
        assertEquals(expected, urlService.getUrlByShortUrl(SHORT_URL));
    }

    @Test
    void shoulThrowExceptionWhenUrlIsEmpty() {
        var dto = new UrlDto();
        dto.setFullUrl(" ");
        assertThrows(NotValidUrlException.class, () -> urlService.saveUrl(dto));
    }

    @Test
    void shoulThrowExceptionWhenUrlIsNull() {
        var dto = new UrlDto();
        assertThrows(NotValidUrlException.class, () -> urlService.saveUrl(dto));
    }

    @Test
    void shoulThrowExceptionWhenUrlIsNotValid() {
        var dto = new UrlDto();
        dto.setFullUrl("https://solocoding");
        assertThrows(NotValidUrlException.class, () -> urlService.saveUrl(dto));
    }

    @Test
    void shouldSaveUrl() {
        var dto = new UrlDto();
        dto.setFullUrl("https://www.solocoding.dev");
        var actual =  urlService.saveUrl(dto);
        assertEquals(getShortUrlStub().getFullUrl(), actual.getFullUrl());
    }

    @Test
    void deleteUrlByIdShouldThrowExceptionWhenNoUrlFound() {
        ObjectId id = getShortUrlStub().getId();
        when(repo.findById(id)).thenReturn(null);
        assertThrows(UrlNotFoundException.class, () -> urlService.deleteUrlById("5f6b3a94684b1858ec6b33f0"));
    }

    @Test
    void shouldDeleteWhenUrlIsFound() {
        ObjectId id = getShortUrlStub().getId();
        when(repo.findById(id)).thenReturn(getShortUrlStub());
        assertDoesNotThrow(() -> urlService.deleteUrlById(id.toHexString()));
    }
}
