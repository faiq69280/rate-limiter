package org.example;

import org.example.model.RequestContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class RateLimiter {
    private ConcurrentMap<String, RequestContext> requestContextMap = new ConcurrentHashMap<>();
    protected int rateLimit;

    public RateLimiter(int rateLimit) { this.rateLimit = rateLimit; }
    public int getRateLimit() { return rateLimit; }
    public void setRateLimit(int rateLimit) { this.rateLimit = rateLimit; }

    public boolean isRequestAllowed(String uniqueRequestId) {
        RequestContext requestContext = requestContextMap.compute(uniqueRequestId,
                (requestId, context) -> {
                    if (context == null) context = new RequestContext();
                    updateOrRefreshContext(context);
                    return context;
                });
        return isRequestAllowed(requestContext);
    }

    protected abstract void updateOrRefreshContext(RequestContext requestContext);
    protected abstract boolean isRequestAllowed(RequestContext requestContext);
}
