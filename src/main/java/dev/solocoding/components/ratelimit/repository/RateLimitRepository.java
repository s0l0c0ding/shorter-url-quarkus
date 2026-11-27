package dev.solocoding.components.ratelimit.repository;

import java.time.ZonedDateTime;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import dev.solocoding.components.ratelimit.entity.RateLimitRecord;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;

public interface RateLimitRepository extends PanacheMongoRepositoryBase<RateLimitRecord, String> {

    default boolean reserveIfWithinQuota(
            String ip,
            ObjectId reservationId,
            ZonedDateTime now,
            int maxPerMinute,
            int maxPerDay) {
        ensureState(ip, now);

        ZonedDateTime minuteCutoff = now.minusMinutes(1);
        ZonedDateTime dayCutoff = now.minusHours(24);

        while (true) {
            RateLimitRecord record = findById(ip);
            if (record == null || !record.isState()) {
                return false;
            }

            record.pruneReservationsBefore(dayCutoff);
            if (record.countReservationsSince(minuteCutoff) >= maxPerMinute
                    || record.countReservationsSince(dayCutoff) >= maxPerDay) {
                return false;
            }

            record.addReservation(reservationId, now);
            record.setCreatedAt(now);
            record.setVersion(record.getVersion() + 1);

            Bson expectedVersion = Filters.and(
                    Filters.eq("_id", ip),
                    Filters.eq("state", true),
                    Filters.eq("version", record.getVersion() - 1));
            long modified = update(Updates.combine(
                    Updates.set("createdAt", now),
                    Updates.set("reservations", record.getReservations()),
                    Updates.set("version", record.getVersion())))
                    .where(expectedVersion);
            if (modified == 1) {
                return true;
            }
        }
    }

    default void deleteReservation(ObjectId id) {
        while (true) {
            RateLimitRecord record = find("reservations.id = ?1", id).firstResult();
            if (record == null || !record.removeReservation(id)) {
                return;
            }
            record.setVersion(record.getVersion() + 1);
            long modified = update(Updates.combine(
                    Updates.set("reservations", record.getReservations()),
                    Updates.set("version", record.getVersion())))
                    .where(Filters.and(
                            Filters.eq("_id", record.getId()),
                            Filters.eq("state", true),
                            Filters.eq("version", record.getVersion() - 1)));
            if (modified == 1) {
                return;
            }
        }
    }

    private void ensureState(String ip, ZonedDateTime now) {
        if (findByIdOptional(ip).isEmpty()) {
            RateLimitRecord state = new RateLimitRecord(ip, now);
            try {
                persist(state);
            } catch (MongoWriteException exception) {
                if (exception.getError().getCode() != 11000) {
                    throw exception;
                }
            }
        };
    }
}
