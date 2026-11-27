package dev.solocoding.config;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import com.mongodb.MongoException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import dev.solocoding.components.ratelimit.repository.RateLimitRepository;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class RateLimitIndexInitializer {

    public static final String COLLECTION_NAME = "rate_limits";
    public static final String CREATED_AT_TTL_INDEX = "createdAt_ttl";

    private final RateLimitRepository repository;

    public RateLimitIndexInitializer(RateLimitRepository repository) {
        this.repository = repository;
    }

    void createRateLimitIndexes(@Observes StartupEvent event) {
        try {
            repository.mongoCollection().createIndex(
                    Indexes.ascending("createdAt"),
                    new IndexOptions().name(CREATED_AT_TTL_INDEX).expireAfter(86400L, TimeUnit.SECONDS));
            log.info("Rate-limit TTL index {} ready", CREATED_AT_TTL_INDEX);
        } catch (MongoException e) {
            log.warn("Unable to create rate-limit indexes for collection {}", COLLECTION_NAME, e);
        }
    }
}
