package dev.solocoding.config;

import static dev.solocoding.common.Constants.EXPIRE_RETENTION_DAYS;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.jboss.logging.Logger;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import dev.solocoding.components.url.repository.UrlRepository;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class UrlIndexInitializer {

    public static final String EXPIRE_TIME_TTL_INDEX = "expireTime_ttl";

    private static final Logger LOG = Logger.getLogger(UrlIndexInitializer.class);

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
            LOG.infof("URL TTL index %s ready (%d days after expiry)", EXPIRE_TIME_TTL_INDEX, EXPIRE_RETENTION_DAYS);
        } catch (RuntimeException e) {
            LOG.warnf(e, "Unable to create URL TTL index %s", EXPIRE_TIME_TTL_INDEX);
        }
    }
}
