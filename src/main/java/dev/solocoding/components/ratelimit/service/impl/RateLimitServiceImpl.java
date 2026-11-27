package dev.solocoding.components.ratelimit.service.impl;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import jakarta.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.MongoException;

import dev.solocoding.components.ratelimit.repository.RateLimitRepository;
import dev.solocoding.components.ratelimit.service.RateLimitService;
import dev.solocoding.exception.ServiceException;
import dev.solocoding.exception.ServiceExceptionEnum;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {
    private final RateLimitRepository repository;
    private final int maxPerMinute;
    private final int maxPerDay;

    public RateLimitServiceImpl(
            RateLimitRepository repository,
            @ConfigProperty(name = "shorterurl.rate-limit.max-per-minute") int maxPerMinute,
            @ConfigProperty(name = "shorterurl.rate-limit.max-per-day") int maxPerDay) {
        this.repository = repository;
        this.maxPerMinute = maxPerMinute;
        this.maxPerDay = maxPerDay;
    }

    @Override
    public ObjectId reserve(String ip) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ObjectId reservationId = new ObjectId();

        try {
            if (!repository.reserveIfWithinQuota(ip, reservationId, now, maxPerMinute, maxPerDay)) {
                throw new ServiceException(ServiceExceptionEnum.RATE_LIMITED);
            }
            return reservationId;
        } catch (MongoException e) {
            releaseAfterFailure(reservationId, e);
            log.error("Rate-limit storage is unavailable error: {}", e.getMessage());
            throw new ServiceException(ServiceExceptionEnum.RATE_LIMIT_UNAVAILABLE);
        }
    }

    @Override
    public void release(ObjectId reservationId) {
        try {
            repository.deleteReservation(reservationId);
        } catch (MongoException e) {
            log.error("Unable to release rate-limit reservation error: {}", e.getMessage());
        }
    }

    private void releaseAfterFailure(ObjectId reservationId, MongoException failure) {
        try {
            repository.deleteReservation(reservationId);
        } catch (MongoException cleanupFailure) {
            failure.addSuppressed(cleanupFailure);
        }
    }
}
