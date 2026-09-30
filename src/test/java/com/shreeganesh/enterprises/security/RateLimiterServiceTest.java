package com.shreeganesh.enterprises.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterServiceTest {

    @Test
    void blocksRequestsAfterLimitInsideWindow() {
        RateLimiterService limiter = new RateLimiterService(
                Clock.fixed(Instant.parse("2026-05-18T00:00:00Z"), ZoneOffset.UTC)
        );

        assertThat(limiter.tryAcquire("login:127.0.0.1", 2, Duration.ofMinutes(1))).isTrue();
        assertThat(limiter.tryAcquire("login:127.0.0.1", 2, Duration.ofMinutes(1))).isTrue();
        assertThat(limiter.tryAcquire("login:127.0.0.1", 2, Duration.ofMinutes(1))).isFalse();
    }

    @Test
    void separatesCountersByKey() {
        RateLimiterService limiter = new RateLimiterService(
                Clock.fixed(Instant.parse("2026-05-18T00:00:00Z"), ZoneOffset.UTC)
        );

        assertThat(limiter.tryAcquire("login:127.0.0.1", 1, Duration.ofMinutes(1))).isTrue();
        assertThat(limiter.tryAcquire("login:127.0.0.1", 1, Duration.ofMinutes(1))).isFalse();
        assertThat(limiter.tryAcquire("login:127.0.0.2", 1, Duration.ofMinutes(1))).isTrue();
    }
}
