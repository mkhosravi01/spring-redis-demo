package com.example.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import redis.clients.jedis.UnifiedJedis;

public class Main {
    public void basicConnection(){
        RedisURI uri = RedisURI.builder().redis("localhost",6379).build();
        RedisClient client = RedisClient.create(uri);
        StatefulRedisConnection<String,String> connection = client.connect();
        RedisCommands<String, String> command = connection.sync();
        command.set("name", "maryam");
        String value = command.get("name");
        System.out.println(value);
        connection.close();
        client.shutdown();
    }
}
