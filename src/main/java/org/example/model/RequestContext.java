package org.example.model;


import java.time.Instant;

public class RequestContext {
    private int requestCount;
    private Instant lastRefreshedAt;

    public RequestContext() { refresh(); }

    public int getRequestCount() { return requestCount; }
    public void increment() { requestCount++; }

    public Instant getLastRefreshedAt() { return lastRefreshedAt; }
    public void setLastRefreshedAt(Instant lastRefreshedAt) { this.lastRefreshedAt = lastRefreshedAt; }

    public void refresh() {
        requestCount = 0;
        lastRefreshedAt = Instant.now();
    }
}
