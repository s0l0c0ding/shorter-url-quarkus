package dev.solocoding.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.solocoding.common.CountryCount;
import dev.solocoding.dto.IpDto;
import dev.solocoding.dto.UrlDto;
import dev.solocoding.entity.Url;
import dev.solocoding.exception.NotValidUrlException;
import dev.solocoding.exception.UrlNotFoundException;
import dev.solocoding.repository.UrlRepository;
import dev.solocoding.service.RequestDetails;

class UrlServiceImplTest {
    
    private final static String SHORT_URL = "6GGfP2";
    private final static String REMOTE_IP = "72.77.138.60";
    private UrlServiceImpl urlService;
    private IpServiceImpl ipService;
    private RequestDetails requestDetails;
    private UrlRepository repo;

    private Url getShortUrlStub() {
        Url url = new Url();
        url.setId(new ObjectId("5f6b3a94684b1858ec6b33f0"));
        url.setFullUrl("https://www.solocoding.dev");
        url.setShortUrl(SHORT_URL);
        url.setCountryCountList(List.of(new CountryCount("US",1)));
        url.setCount(0);
        url.setVersion(0L);
        return url;
    }

    private IpDto getIpDtoStub() {
        IpDto ip = new IpDto();
        ip.setCountryCode("US");
        ip.setCountryName("UNITED STATES");
        ip.setId("507f191e810c19729de860ea");
        ip.setIpFrom(1213041208l);
        ip.setIpTo(1213041215l);
        return ip;
    }

    @BeforeEach
    void setUp() {
        repo = mock(UrlRepository.class);
        ipService = mock(IpServiceImpl.class);
        requestDetails = mock(RequestDetailsImpl.class);
        urlService = new UrlServiceImpl(repo, ipService, requestDetails);
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

    @Test
    void getAndRedirectShouldAddCountryWhenIpIsFound() {
        when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(getShortUrlStub()));
        when(requestDetails.getRemoteAddress()).thenReturn(REMOTE_IP);
        when(ipService.getByIp(REMOTE_IP)).thenReturn(getIpDtoStub());
        UrlDto actual = urlService.getAndRedirect(SHORT_URL);
        assertEquals(2, actual.getCountryCountList().get(0).getCount());
    }

    @Test
    void getAndRedirectShouldAddCountryWhenIpIsNotFound() {
        when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(getShortUrlStub()));
        when(requestDetails.getRemoteAddress()).thenReturn(REMOTE_IP);
        when(ipService.getByIp(REMOTE_IP)).thenReturn(new IpDto());
        UrlDto actual = urlService.getAndRedirect(SHORT_URL);
        assertEquals(1, actual.getCountryCountList().get(0).getCount());
    }

    @Test
    void getAndRedirectShouldAddCountryWhenIpIsFoundWithCountryListEmpty() {
        Url urlWithEmptyCountry = getShortUrlStub();
        urlWithEmptyCountry.setCountryCountList(null);
        when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(urlWithEmptyCountry));
        when(requestDetails.getRemoteAddress()).thenReturn(REMOTE_IP);
        when(ipService.getByIp(REMOTE_IP)).thenReturn(getIpDtoStub());
        UrlDto actual = urlService.getAndRedirect(SHORT_URL);
        assertEquals(1, actual.getCountryCountList().get(0).getCount());
    }
}
