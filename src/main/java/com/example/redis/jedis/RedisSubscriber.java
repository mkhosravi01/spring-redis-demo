package com.example.redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber {
    public static void main(String[] args) {
        // Connect to Redis server
        try(Jedis jedis = new Jedis("localhost",6379)){
            // Define a new JedisPubSub instance to handle messages
            JedisPubSub pubSub = new JedisPubSub() {
             public void onMessage(String channel, String message) {
                 System.out.println("channel: " + channel + " message: " + message);
             }
            };
            // Subscribe to the "my-channel" channel
            jedis.subscribe(pubSub, "redis");
        }
    }
}
