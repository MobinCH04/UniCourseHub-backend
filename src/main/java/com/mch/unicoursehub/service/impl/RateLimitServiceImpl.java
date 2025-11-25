package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.TooManyRequestsException;
import com.mch.unicoursehub.service.RateLimitService;
import org.springframework.stereotype.Service;
import io.github.bucket4j.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

    /**
     * Implementation of the RateLimitService interface.
     * Manages rate-limiting functionality for APIs using Bucket4j.
     */
    @Service
    public class RateLimitServiceImpl implements RateLimitService {

        /**
         * A thread-safe map for storing API keys and their associated buckets.
         */
        Map<String, Bucket> privateRoute = new ConcurrentHashMap<>();

        /**
         * Resolves or creates a Bucket for the given API key.
         *
         * @param apiKey the unique API key for the client.
         * @return the Bucket associated with the API key.
         */
        public Bucket resolveBucket(String apiKey) {
            return privateRoute.computeIfAbsent(apiKey, id -> newBucket(id, 60, 60, Duration.ofMinutes(1)));
        }

        public void removeBucket(String username) {
            if (!privateRoute.containsKey(username))
                return;

            privateRoute.remove(username);
        }

        private Bucket authResolveBucket(String username) {
            return privateRoute.computeIfAbsent(username, id -> newBucket(id, 4, 1, Duration.ofMinutes(5)));
        }

        /**
         * Creates a new Bucket with the specified capacity and refill rate.
         *
         * @param s the identifier for the bucket (usually the API key).
         * @param capacity the maximum capacity of the bucket.
         * @param tokens the number of tokens refilled per interval.
         * @return the newly created Bucket.
         */
        private Bucket newBucket(String s, int capacity, int tokens, Duration duration) {
            return Bucket4j.builder()
                    .addLimit(Bandwidth.classic(capacity, Refill.intervally(tokens, duration)))
                    .build();
        }

        public void applyAuthRateLimit(String username) {

            Bucket bucket = authResolveBucket(username);

            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (!probe.isConsumed())
                throw new TooManyRequestsException();

        }

    }
