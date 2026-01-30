package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.TooManyRequestsException;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RateLimitServiceImplTest {

    private RateLimitServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RateLimitServiceImpl();
    }

    // ------------------ resolveBucket ------------------

    @Test
    void resolveBucket_shouldCreateNewBucketIfNotExists() {
        String apiKey = "api-123";

        Bucket bucket = service.resolveBucket(apiKey);

        assertThat(bucket).isNotNull();
        // مطمئن شو که bucket داخل map ذخیره شده
        assertThat(service.privateRoute.containsKey(apiKey)).isTrue();
    }

    @Test
    void resolveBucket_shouldReturnExistingBucket() {
        String apiKey = "api-456";

        Bucket first = service.resolveBucket(apiKey);
        Bucket second = service.resolveBucket(apiKey);

        assertThat(first).isSameAs(second);
    }

    // ------------------ removeBucket ------------------

    @Test
    void removeBucket_shouldRemoveExistingBucket() {
        String apiKey = "api-789";
        service.resolveBucket(apiKey);

        service.removeBucket(apiKey);

        assertThat(service.privateRoute.containsKey(apiKey)).isFalse();
    }

    @Test
    void removeBucket_shouldDoNothingIfNotExists() {
        String apiKey = "non-existent";
        // فقط اطمینان از این که خطا نمی‌دهد
        service.removeBucket(apiKey);
    }

    // ------------------ applyAuthRateLimit ------------------

    @Test
    void applyAuthRateLimit_shouldConsumeToken() {
        String username = "user1";

        service.applyAuthRateLimit(username); // اولین مصرف
        // بعد از اولین مصرف، هنوز bucket ایجاد شده و توکن‌ها کاهش یافته
        assertThat(service.privateRoute.containsKey(username)).isTrue();
    }

    @Test
    void applyAuthRateLimit_whenExceeded_shouldThrow() {
        String username = "user2";

        // bucket auth: 4 capacity, 1 token per 5 min (پس فقط یکبار مصرف می‌شود بدون refill)
        // ما 5 بار مصرف می‌کنیم تا خطا ایجاد شود
        for (int i = 0; i < 4; i++) {
            service.applyAuthRateLimit(username); // 4 بار مصرف موفق
        }

        assertThrows(TooManyRequestsException.class, () -> service.applyAuthRateLimit(username));
    }

    @Test
    void authResolveBucket_shouldReturnSameBucketForSameUser() throws Exception {
        // متد private را با بازتاب فراخوانی می‌کنیم
        var method = RateLimitServiceImpl.class.getDeclaredMethod("authResolveBucket", String.class);
        method.setAccessible(true);

        Object bucket1 = method.invoke(service, "user3");
        Object bucket2 = method.invoke(service, "user3");

        assertThat(bucket1).isSameAs(bucket2);
    }

    @Test
    void newBucket_shouldCreateBucketWithGivenCapacity() throws Exception {

        var method = RateLimitServiceImpl.class.getDeclaredMethod(
                "newBucket", String.class, int.class, int.class, Duration.class
        );
        method.setAccessible(true);

        Object bucket = method.invoke(service, "id1", 5, 1, Duration.ofMinutes(1));
        assertThat(bucket).isNotNull();
    }
}
