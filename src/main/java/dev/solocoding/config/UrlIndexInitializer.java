package dev.solocoding.config;

import static dev.solocoding.common.Constants.EXPIRE_RETENTION_DAYS;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import dev.solocoding.components.url.repository.UrlRepository;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class UrlIndexInitializer {

    public static final String EXPIRE_TIME_TTL_INDEX = "expireTime_ttl";

    private final UrlRepository repo;

    public UrlIndexInitializer(UrlRepository repo) {
        this.repo = repo;
    }

    void createExpireTimeIndex(@Observes StartupEvent event) {
        try {
            repo.mongoCollection().createIndex(
                    Indexes.ascending("expireTime"),
                    new IndexOptions().name(EXPIRE_TIME_TTL_INDEX)
                            .expireAfter(EXPIRE_RETENTION_DAYS, TimeUnit.DAYS));
            log.info("URL TTL index {} ready ({} days after expiry)", EXPIRE_TIME_TTL_INDEX, EXPIRE_RETENTION_DAYS);
        } catch (RuntimeException e) {
            log.warn("Unable to create URL TTL index {}", EXPIRE_TIME_TTL_INDEX, e);
        }
    }
}
