package dev.solocoding.components.ratelimit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.MongoException;

import dev.solocoding.components.ratelimit.repository.RateLimitRepository;
import dev.solocoding.components.ratelimit.service.impl.RateLimitServiceImpl;
import dev.solocoding.exception.ServiceException;
import dev.solocoding.exception.error.ApiError;

class RateLimitServiceImplTest {

    private RateLimitRepository repository;
    private RateLimitServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(RateLimitRepository.class);
        service = new RateLimitServiceImpl(repository, 5, 10);
    }

    @Test
    // AC-3.4
    void shouldRejectSixthCreationWithinOneMinute() {
        when(repository.reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(5), eq(10)))
                .thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> service.reserve("192.0.2.1"));

        assertEquals(7, ((ApiError) exception.toResponseEntity().getEntity()).getCode());
        assertEquals(429, exception.toResponseEntity().getStatus());
        verify(repository).reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(5), eq(10));
        verify(repository, never()).deleteReservation(any(ObjectId.class));
    }

    @Test
    // AC-3.5
    void shouldRejectEleventhCreationWithinOneDay() {
        when(repository.reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(5), eq(10)))
                .thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> service.reserve("192.0.2.1"));

        assertEquals(7, ((ApiError) exception.toResponseEntity().getEntity()).getCode());
        assertEquals(429, exception.toResponseEntity().getStatus());
        verify(repository).reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(5), eq(10));
    }

    @Test
    // AC-3.7
    void shouldFailClosedAndReleaseReservationWhenAtomicReservationFails() {
        when(repository.reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(5), eq(10)))
                .thenThrow(new MongoException("database unavailable"));

        ServiceException exception = assertThrows(ServiceException.class, () -> service.reserve("192.0.2.1"));

        assertEquals(8, ((ApiError) exception.toResponseEntity().getEntity()).getCode());
        assertEquals(503, exception.toResponseEntity().getStatus());
        verify(repository).deleteReservation(any(ObjectId.class));
    }

    @Test
    // AC-3.6
    void shouldKeepReservationWhenBothWindowsAreWithinQuota() {
        when(repository.reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(5), eq(10)))
                .thenReturn(true);

        ObjectId reservation = service.reserve("192.0.2.1");

        verify(repository, never()).deleteReservation(any(ObjectId.class));
        verify(repository).reserveIfWithinQuota(
                eq("192.0.2.1"), eq(reservation), any(ZonedDateTime.class), eq(5), eq(10));
        assertNotNull(reservation);
    }

    @Test
    // AC-3.4
    void shouldUseConfiguredQuotaLimits() {
        service = new RateLimitServiceImpl(repository, 1, 2);
        when(repository.reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(1), eq(2)))
                .thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> service.reserve("192.0.2.1"));

        assertEquals(7, ((ApiError) exception.toResponseEntity().getEntity()).getCode());
        verify(repository).reserveIfWithinQuota(
                eq("192.0.2.1"), any(ObjectId.class), any(ZonedDateTime.class), eq(1), eq(2));
    }
}
