package org.example.concrete;

import org.example.RateLimiter;
import org.example.model.RequestContext;

import java.time.Duration;
import java.time.Instant;

public class FixedWindowRateLimiter extends RateLimiter {
    private int timeLimitInSeconds;

    public FixedWindowRateLimiter(int timeLimitInSeconds, int rateLimit) {
        super(rateLimit);
        this.timeLimitInSeconds = timeLimitInSeconds;
    }

    public int getTimeLimitInSeconds() { return timeLimitInSeconds; }
    public void setTimeLimitInSeconds(int timeLimitInSeconds) { this.timeLimitInSeconds = timeLimitInSeconds; }

    @Override protected void updateOrRefreshContext(RequestContext requestContext) {
        if (Duration.between(requestContext.getLastRefreshedAt(), Instant.now()).toSeconds() > timeLimitInSeconds) {
            requestContext.refresh();
        }
        requestContext.increment();
    }

    @Override  protected boolean isRequestAllowed(RequestContext requestContext) {
        return requestContext.getRequestCount() <= rateLimit;
    }
}
