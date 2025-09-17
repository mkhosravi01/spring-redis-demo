package com.example.redis.redisdemo.redisdemo;

import com.example.redis.jedis.TokenBucketRateLimiter;
import com.redis.testcontainers.RedisContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import java.util.concurrent.TimeUnit;

public class TokenBuketRateLimiterJedisTest {
    private  static RedisContainer redisContainer;
    private Jedis jedis;
    private TokenBucketRateLimiter tokenBucketRateLimiter;

    @BeforeAll
    static  void startContainer(){
        redisContainer = new RedisContainer("redis:latest");
        redisContainer.withExposedPorts(6379).start();

    }

    @BeforeEach
    void setup(){
        jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
        jedis.flushAll();

    }

    @AfterEach
    void tearDown(){
        jedis.close();
    }

    @Test
    void shouldAllowRequestsWithinBucketCapacity() {
        tokenBucketRateLimiter = new TokenBucketRateLimiter(jedis,5,1.0);
        for(int i=1; i<=5; i++){
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed("client-1"))
                    .withFailMessage("Request %d  is should be allowed", i)
                    .isTrue();

        }
    }

    @Test
    void shouldDenyRequestsOnceBucketIsEmpty() {
        tokenBucketRateLimiter = new TokenBucketRateLimiter(jedis,5,1.0);
        for(int i=1; i<=5; i++){
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed("client-1"))
                    .withFailMessage("Request %d  is should be allowd within bucket capacity", i)
                    .isTrue();
        }
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed("client-1"))
                .withFailMessage("Request beyond bucket capacity should be denied")
                .isFalse();
    }

    @Test
    void shouldRefillTokensGraduallyAndAllowRequestsOverTime() throws InterruptedException {
        tokenBucketRateLimiter = new TokenBucketRateLimiter(jedis, 5, 1.0);
        String clientId = "client-1";

        for (int i = 1; i <= 5; i++) {
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                    .withFailMessage("Request %d should be allowed within bucket capacity", i)
                    .isTrue();
        }
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                .withFailMessage("Request beyond bucket capacity should be denied")
                .isFalse();

        TimeUnit.SECONDS.sleep(2);

        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                .withFailMessage("Request after partial refill should be allowed")
                .isTrue();
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                .withFailMessage("Second request after partial refill should be allowed")
                .isTrue();
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                .withFailMessage("Request beyond available tokens should be denied")
                .isFalse();
    }

    @Test
    void shouldHandleMultipleClientsIndependently() {
        tokenBucketRateLimiter = new TokenBucketRateLimiter(jedis, 5, 1.0);

        String clientId1 = "client-1";
        String clientId2 = "client-2";

        for (int i = 1; i <= 5; i++) {
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId1))
                    .withFailMessage("Client 1 request %d should be allowed", i)
                    .isTrue();
        }
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId1))
                .withFailMessage("Client 1 request beyond bucket capacity should be denied")
                .isFalse();

        for (int i = 1; i <= 5; i++) {
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId2))
                    .withFailMessage("Client 2 request %d should be allowed", i)
                    .isTrue();
        }
    }

    @Test
    void shouldRefillTokensUpToCapacityWithoutExceedingIt() throws InterruptedException {
        int capacity = 3;
        double refillRate = 2.0;
        String clientId = "client-1";
        tokenBucketRateLimiter = new TokenBucketRateLimiter(jedis, capacity, refillRate);

        for (int i = 1; i <= capacity; i++) {
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                    .withFailMessage("Request %d should be allowed within initial bucket capacity", i)
                    .isTrue();
        }
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                .withFailMessage("Request beyond bucket capacity should be denied")
                .isFalse();

        TimeUnit.SECONDS.sleep(3);

        for (int i = 1; i <= capacity; i++) {
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                    .withFailMessage("Request %d should be allowed as bucket refills up to capacity", i)
                    .isTrue();
        }
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                .withFailMessage("Request beyond bucket capacity should be denied")
                .isFalse();
    }

    @Test
    void testRateLimitDeniedRequestsAreNotCounted() {
        int capacity = 3;
        double refillRate = 0.5;
        String clientId = "client-1";
        tokenBucketRateLimiter = new TokenBucketRateLimiter(jedis, capacity, refillRate);

        for (int i = 1; i <= capacity; i++) {
            Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                    .withFailMessage("Request %d should be allowed", i)
                    .isTrue();
        }
        Assertions.assertThat(tokenBucketRateLimiter.isAllowed(clientId))
                .withFailMessage("This request should be denied")
                .isFalse();

        String key = "rate_limit:" + clientId + ":count";
        int requestCount = Integer.parseInt(jedis.get(key));
        Assertions.assertThat(requestCount)
                .withFailMessage("The count should match remaining tokens and not include denied requests")
                .isEqualTo(0);
    }
}
