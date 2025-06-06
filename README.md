# Rate Limiter Library

A simple, extensible Java rate limiting library with support for fixed window rate limiting. Designed for backend services to control request rates per client or user.

---

## Features

- Abstract base `RateLimiter` class to implement different rate limiting strategies.  
- Thread-safe with concurrent request handling.  
- Example implementation: `FixedWindowRateLimiter`.  
- Uses `RequestContext` to track request counts and window refresh timestamps.  
- Easily extendable for custom rate limiting algorithms.  
- Tested with concurrent request simulation.

---

## Project Structure
org.example
│
├── RateLimiter.java (Abstract base class)
├── concrete
│ └── FixedWindowRateLimiter.java (Fixed window implementation)
└── model
└── RequestContext.java (Tracks request count and timestamps)



---

## Usage

### Add Dependency

If you have installed the library to your local Maven repository, add the following to your project's `pom.xml`:

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>rate-limiter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
## Example: Using Fixed Window Rate Limiter

```java
import org.example.concrete.FixedWindowRateLimiter;

public class RateLimiterExample {
    public static void main(String[] args) throws InterruptedException {
        final int windowSeconds = 60;  // 1 minute window
        final int maxRequests = 10;    // max 10 requests per window

        FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter(windowSeconds, maxRequests);

        String userId = "user-123";

        for (int i = 0; i < 15; i++) {
            boolean allowed = rateLimiter.isRequestAllowed(userId);
            System.out.println("Request " + (i + 1) + " allowed? " + allowed);
            Thread.sleep(200); // simulate request spacing
        }
    }
}
```
## Testing
Concurrent request handling is covered in unit tests using CompletableFuture to simulate multiple threads.
The test ensures requests beyond the rate limit are rejected within the time window, and allowed after the window resets.

Build & Installation
Build the JAR using IntelliJ or Maven:
```bash
mvn clean install
```
