import org.example.RateLimiter;
import org.example.concrete.FixedWindowRateLimiter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class FixedWindowRateLimiterTest {
    //30 concurrent requests per requestId
    public final int TEST_RATE_LIMIT_VALUE = 30;
    //60 seconds of fixed window size
    public final int TEST_TIME_WINDOW_VALUE_IN_SECONDS = 60;
    //number of concurrent requests simulated in time_limit_value
    public final int TEST_CONCURRENT_REQUESTS_COUNT = 120;
    //number of requests so far to keep track
    private AtomicInteger requestCountSoFar;

    @Before
    public void setup() {
        requestCountSoFar = new AtomicInteger();
        requestCountSoFar.set(0);
    }

    @After
    public void tearDown() {
        requestCountSoFar = null;
    }

    @Test public void testWithOneUser() throws Exception {
        final String requestId = UUID.randomUUID().toString();
        final RateLimiter rateLimiter = new FixedWindowRateLimiter(TEST_TIME_WINDOW_VALUE_IN_SECONDS, TEST_RATE_LIMIT_VALUE);
        final Instant checkpoint = Instant.now();

        CompletableFuture<?>[] futures = IntStream.range(0, TEST_CONCURRENT_REQUESTS_COUNT)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    int count = requestCountSoFar.incrementAndGet();
                    boolean rateLimiterRequestAllowed = rateLimiter.isRequestAllowed(requestId);
                    boolean expectedAllowState = count <= TEST_RATE_LIMIT_VALUE;
                    if (Duration.between(checkpoint, Instant.now()).getSeconds() <= TEST_TIME_WINDOW_VALUE_IN_SECONDS) {
                        Logger.getGlobal().log(Level.INFO, "within time window : requestCount : %d, rateLimiterRequestAllowed : %b".formatted(count, rateLimiterRequestAllowed));
                        Assert.assertEquals(expectedAllowState, rateLimiterRequestAllowed);
                    }
                    else {
                        Logger.getGlobal().log(Level.INFO, "outside time window : requestCount : %d, rateLimiterRequestAllowed : %b".formatted(count, rateLimiterRequestAllowed));
                        Assert.assertTrue(rateLimiterRequestAllowed);
                    }
                }))
                .toArray(CompletableFuture[]::new);
        // Wait for all tasks to finish
        CompletableFuture.allOf(futures).join();

        Duration elapsed = Duration.between(checkpoint, Instant.now());
        long elapsedSeconds = elapsed.getSeconds();
        long remainingSeconds = TEST_TIME_WINDOW_VALUE_IN_SECONDS - (elapsedSeconds % TEST_TIME_WINDOW_VALUE_IN_SECONDS);
        Logger.getGlobal().log(Level.INFO, "Remaining seconds: %d".formatted(remainingSeconds));
        Thread.sleep((remainingSeconds+1) * 1000);
        Assert.assertTrue(rateLimiter.isRequestAllowed(requestId));
    }

}
