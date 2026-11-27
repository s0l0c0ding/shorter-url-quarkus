package dev.solocoding.components.ratelimit.service;

import org.bson.types.ObjectId;

public interface RateLimitService {

    ObjectId reserve(String ip);

    void release(ObjectId reservationId);
}
