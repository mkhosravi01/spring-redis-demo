package com.example.redis.jedis;

import redis.clients.jedis.Jedis;

public class RedisPublisher {
    public static void main(String[] args) {
        // Connect to Redis server
        try(Jedis jedis = new Jedis("localhost",6379)){
            // Publish a message to the "my-channel" channel
            jedis.publish("redis","sara");
            System.out.println("Published!");
        }
    }
}
