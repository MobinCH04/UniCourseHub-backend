package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.TooManyRequestsException;
import com.mch.unicoursehub.service.RateLimitService;
import org.springframework.stereotype.Service;
import io.github.bucket4j.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

    /**
     * Implementation of the {@link RateLimitService} interface.
     *
     * <p>Manages API rate-limiting functionality using Bucket4j. Each user or API key
     * is associated with a token bucket that enforces request limits within a
     * specified time window.</p>
     *
     * <p>Provides separate handling for general API requests and authenticated
     * endpoints with stricter limits.</p>
     */
    @Service
    public class RateLimitServiceImpl implements RateLimitService {

        /**
         * A thread-safe map for storing API keys and their associated buckets.
         */
        Map<String, Bucket> privateRoute = new ConcurrentHashMap<>();

        /**
         * Resolves or creates a token bucket for the given API key.
         * If a bucket does not already exist, a new one is created with the specified
         * capacity and refill rate.
         *
         * @param apiKey the unique API key for the client
         * @return the {@link Bucket} associated with the API key
         */
        public Bucket resolveBucket(String apiKey) {
            return privateRoute.computeIfAbsent(apiKey, id -> newBucket(id, 60, 60, Duration.ofMinutes(1)));
        }

        /**
         * Removes the bucket associated with the given username, if it exists.
         *
         * @param username the username whose bucket should be removed
         */
        public void removeBucket(String username) {
            if (!privateRoute.containsKey(username))
                return;

            privateRoute.remove(username);
        }

        /**
         * Resolves or creates a token bucket for an authenticated user.
         * Limits are stricter for authenticated endpoints.
         *
         * @param username the username of the authenticated client
         * @return the {@link Bucket} associated with the user
         */
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

        /**
         * Applies the authenticated rate limit for a given user.
         * Consumes a token from the user's bucket; if no tokens remain,
         * a {@link TooManyRequestsException} is thrown.
         *
         * @param username the username of the client
         * @throws TooManyRequestsException if the request exceeds the allowed rate
         */
        public void applyAuthRateLimit(String username) {

            Bucket bucket = authResolveBucket(username);

            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (!probe.isConsumed())
                throw new TooManyRequestsException();

        }

    }
