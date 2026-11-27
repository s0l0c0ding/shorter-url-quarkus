package dev.solocoding.components.ratelimit.entity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MongoEntity(collection = "rate_limits")
public class RateLimitRecord {

    // The MongoDB document ID is the IP address.
    private String id;
    private boolean state;
    private ZonedDateTime createdAt;
    private List<Reservation> reservations = new ArrayList<>();
    private long version;

    public RateLimitRecord(String ip, ZonedDateTime createdAt) {
        this.id = ip;
        this.state = true;
        this.createdAt = createdAt;
    }

    public long countReservationsSince(ZonedDateTime cutoff) {
        return reservations.stream()
                .filter(reservation -> reservation.getCreatedAt() != null
                        && !reservation.getCreatedAt().isBefore(cutoff))
                .count();
    }

    public void pruneReservationsBefore(ZonedDateTime cutoff) {
        reservations.removeIf(reservation -> reservation.getCreatedAt() == null
                || reservation.getCreatedAt().isBefore(cutoff));
    }

    public void addReservation(ObjectId reservationId, ZonedDateTime createdAt) {
        reservations.add(new Reservation(reservationId, createdAt));
    }

    public boolean removeReservation(ObjectId reservationId) {
        return reservations.removeIf(reservation -> reservationId.equals(reservation.getId()));
    }

    @Data
    @NoArgsConstructor
    public static class Reservation {
        private ObjectId id;
        private ZonedDateTime createdAt;

        public Reservation(ObjectId id, ZonedDateTime createdAt) {
            this.id = id;
            this.createdAt = createdAt;
        }
    }
}
