package com.example.redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TokenBucketRateLimiter {
    private final Jedis jedis;
    private final int bucketCapacity; // Maximum tokens the bucket can hold
    private final double refillRate; // Tokens refilled per second

    public TokenBucketRateLimiter(Jedis jedis, int bucketCapacity, double refillRate) {
        this.jedis = jedis;
        this.bucketCapacity = bucketCapacity;
        this.refillRate = refillRate;
    }

    public boolean isAllowed(String clientId){
        String keyCount = "rate_limit:" + clientId + ":count";
        String keyLastRefill = "rate_limit:" + clientId + ":lastRefill";
        long currentTime = System.currentTimeMillis();

        // Fetch current state
        Transaction transaction = jedis.multi();
        transaction.get(keyLastRefill);
        transaction.get(keyCount);
        var result = transaction.exec();

        long lastRefillTime = result.get(0) != null ? Long.parseLong((String)result.get(0)) : currentTime;
        int tokenCount = result.get(1) != null ? Integer.parseInt((String)result.get(1)) : bucketCapacity;

        // Refill tokens
        long elapsedTimeMS= currentTime - lastRefillTime;
        double elapsedTimeSecs =  elapsedTimeMS / 1000.0;
        int tokenToAdd = (int) (elapsedTimeSecs * refillRate);
        tokenCount = Math.min(bucketCapacity, tokenCount + tokenToAdd );

        // Check if the request is allowed
        boolean allowed = tokenCount > 0;
        if(allowed){
            tokenCount--; // Consume one token
        }

        // Update Redis state
        transaction = jedis.multi();
        transaction.set(keyLastRefill, String.valueOf(currentTime));
        transaction.set(keyCount, String.valueOf(tokenCount));
        transaction.exec();

        return allowed;



    }
}
