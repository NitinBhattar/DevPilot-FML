package com.devpilotfml.backend.service.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Helps in not bombarding GitHub with requests ultimately getting Too Many Requests
@Component
public class GithubRateLimiter {
    @Value("${app.api.github.api-delay-ms}")
    private long delayMs;

    public void pause() {
        if (delayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while rate limiting", e);
        }
    }
}
