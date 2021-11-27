package dev.solocoding.components.url.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import dev.solocoding.common.CountryCount;
import dev.solocoding.components.ip.dto.IpDto;
import dev.solocoding.components.ip.service.IpServiceImpl;
import dev.solocoding.components.request.service.RequestDetails;
import dev.solocoding.components.request.service.RequestDetailsImpl;
import dev.solocoding.components.url.dto.UrlDto;
import dev.solocoding.components.url.entity.Url;
import dev.solocoding.components.url.repository.UrlRepository;
import dev.solocoding.components.url.service.impl.UrlServiceImpl;
import dev.solocoding.exception.ServiceException;
import io.vertx.mutiny.core.eventbus.EventBus;

class UrlServiceImplTest {
    
    private final static String SHORT_URL = "6GGfP2";
    private final static String REMOTE_IP = "72.77.138.60";
    private UrlServiceImpl urlService;
    private IpServiceImpl ipService;
    private RequestDetails requestDetails;
    private UrlRepository repo;
    private EventBus bus;

    private Url getShortUrlStub() {
        Url url = new Url();
        url.setId(new ObjectId("5f6b3a94684b1858ec6b33f0"));
        url.setFullUrl("https://www.solocoding.dev");
        url.setShortUrl(SHORT_URL);
        url.setCountryCountList(List.of(new CountryCount("US",1), new CountryCount("IT",1)));
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
        bus = mock(EventBus.class);
        urlService = new UrlServiceImpl(repo, ipService, requestDetails, bus);
    }

    @Nested
    @DisplayName("GetUrlByShortUrl")
    class GetUrlByShortUrl {

        @Test
        void shoulThrowExceptionWhenNoUrlFound() {
            when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.empty());
            assertThrows(ServiceException.class, () -> urlService.getUrlByShortUrl(SHORT_URL));
        }
    
        @Test
        void shouldGetUrlByShortUrl() {
            when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(getShortUrlStub()));
            var stub = getShortUrlStub();
            stub.setExpireTime(ZonedDateTime.now(ZoneOffset.UTC).plusDays(30));
            UrlDto expected = new UrlDto(stub);
            assertEquals(expected.getFullUrl(), urlService.getUrlByShortUrl(SHORT_URL).getFullUrl());
        }
    
        @Test
        void shoulThrowExceptionWhenUrlIsExpired() {
            var stub = getShortUrlStub();
            stub.setExpireTime(ZonedDateTime.now(ZoneOffset.UTC).minusDays(1));
            
            when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(stub));
    
            assertThrows(ServiceException.class, () -> urlService.getUrlByShortUrl(SHORT_URL));
        }
    
        @Test
        void shoulThrowExceptionWhenUrlIsExpiredFrom60Days() {
            var stub = getShortUrlStub();
            stub.setExpireTime(ZonedDateTime.now(ZoneOffset.UTC).minusDays(60));
            
            when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(stub));
            doNothing().when(repo).delete(stub);
    
            assertThrows(ServiceException.class, () -> urlService.getUrlByShortUrl(SHORT_URL));
        }
    }

    @Nested
    @DisplayName("SaveUrl")
    class SaveUrl {

        @Test
        void shoulThrowExceptionWhenUrlIsEmpty() {
            var dto = new UrlDto();
            dto.setFullUrl(" ");
            assertThrows(ServiceException.class, () -> urlService.saveUrl(dto));
        }
    
        @Test
        void shoulThrowExceptionWhenUrlIsNull() {
            var dto = new UrlDto();
            assertThrows(ServiceException.class, () -> urlService.saveUrl(dto));
        }
    
        @Test
        void shoulThrowExceptionWhenUrlIsNotValid() {
            var dto = new UrlDto();
            dto.setFullUrl("https://solocoding");
            assertThrows(ServiceException.class, () -> urlService.saveUrl(dto));
        }
    
        @Test
        void shoulThrowExceptionWhenUrlLegnthIsNotValid() {
            var dto = new UrlDto();
            final var url = "https://www.solocoding" + "g".repeat(350)+ ".dev";
            dto.setFullUrl(url);
            assertThrows(ServiceException.class, () -> urlService.saveUrl(dto));
        }
    
        @Test
        void shouldSaveUrl() {
            var dto = new UrlDto();
            dto.setFullUrl("https://www.solocoding.dev");
            var actual =  urlService.saveUrl(dto);
            assertEquals(getShortUrlStub().getFullUrl(), actual.getFullUrl());
            assertNotNull(actual.getExpireTime());
        }
    }
    
    @Test
    void deleteUrlByIdShouldThrowExceptionWhenNoUrlFound() {
        ObjectId id = getShortUrlStub().getId();
        when(repo.findById(id)).thenReturn(null);
        assertThrows(ServiceException.class, () -> urlService.deleteUrlById("5f6b3a94684b1858ec6b33f0"));
    }

    @Test
    void shouldDeleteWhenUrlIsFound() {
        ObjectId id = getShortUrlStub().getId();
        when(repo.findById(id)).thenReturn(getShortUrlStub());
        assertDoesNotThrow(() -> urlService.deleteUrlById(id.toHexString()));
    }

    @Test
    void updateCounterShouldAddCountryWhenIpIsFound() {
        when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(getShortUrlStub()));
        when(requestDetails.getRemoteAddress()).thenReturn(REMOTE_IP);
        when(ipService.getByIp(REMOTE_IP)).thenReturn(getIpDtoStub());
        urlService.updateCounter(getShortUrlStub());
        verify(ipService, times(1)).getByIp(REMOTE_IP);
        verify(repo, times(1)).update(any(Url.class));
    }

    @Test
    void updateUrlByShortIdShouldUpdate() {
        var dto = new UrlDto();
        dto.setFullUrl(getShortUrlStub().getFullUrl());
        when(repo.findByShortUrl(SHORT_URL)).thenReturn(Optional.of(getShortUrlStub()));
        
        urlService.updateUrlByShortId(SHORT_URL, dto);
        
        verify(repo, times(1)).update(any(Url.class));
    }
}
