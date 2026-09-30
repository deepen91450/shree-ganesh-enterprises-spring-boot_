package com.shreeganesh.enterprises.security;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimiterService() {
        this(Clock.systemUTC());
    }

    RateLimiterService(Clock clock) {
        this.clock = clock;
    }

    public boolean tryAcquire(String key, int maxRequests, Duration windowDuration) {
        Instant now = clock.instant();
        cleanup(now);

        Window window = windows.compute(key, (unused, existing) -> {
            if (existing == null || !existing.expiresAt.isAfter(now)) {
                return new Window(1, now.plus(windowDuration));
            }

            existing.requests++;
            return existing;
        });

        return window.requests <= maxRequests;
    }

    private void cleanup(Instant now) {
        Iterator<Map.Entry<String, Window>> iterator = windows.entrySet().iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().getValue().expiresAt.isAfter(now)) {
                iterator.remove();
            }
        }
    }

    private static class Window {
        private int requests;
        private final Instant expiresAt;

        private Window(int requests, Instant expiresAt) {
            this.requests = requests;
            this.expiresAt = expiresAt;
        }
    }
}
