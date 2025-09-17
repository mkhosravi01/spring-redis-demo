package com.example.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.UnifiedJedis;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Main {



    public void redisDelayQueueTest_lettuce(){
        RedisURI uri = RedisURI.builder().redis("localhost",6379).build();
        RedisClient client = RedisClient.create(uri);
        StatefulRedisConnection<String,String> connection = client.connect();
        RedisCommands<String, String> command = (RedisCommands<String, String>) connection.sync();
//        String key = "delay_queue";
//        String OrderId1 = UUID.randomUUID().toString();
//        command.zadd(key,System.currentTimeMillis()+5000,OrderId1);
//        String OrderId2 = UUID.randomUUID().toString();
//        command.zadd(key,System.currentTimeMillis()+5000,OrderId2);
//        new Thread(){
//            public void run(){
//                while(true){
//                    List<String> resultList;
//                    resultList = command.zrange(key,0,1);
//                }
//            }
//        }
        command.set("name", "maryam");
        String value = command.get("name");
        System.out.println(value);
        connection.close();
        client.shutdown();
    }
    public void redisDelayQueueTest_jedis(){
        Jedis jedis = new Jedis("localhost");
        String key = "delay_queue";
        String OrderId1 = UUID.randomUUID().toString();
        jedis.zadd(key,System.currentTimeMillis()+5000,OrderId1);
        String OrderId2 = UUID.randomUUID().toString();
        jedis.zadd(key,System.currentTimeMillis()+5000,OrderId2);
        new Thread(){
            public void run(){
                while(true){
                    List<String> resultSetList;
                    resultSetList = jedis.zrangeByScore(key, System.currentTimeMillis(),0);
                    if(resultSetList.isEmpty()){
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e){
                            e.printStackTrace();
                            break;
                        }

                    }else {
                        if(jedis.zrem(key,resultSetList.iterator().next()) > 0){
                            String orderList = resultSetList.iterator().next();
                            System.out.println("log:"+orderList);
                            handleMsg(orderList);
                        }
                    }

                }
            }
        }.start();
    }
    public void handleMsg(String msg) {
        System.out.println(msg);
    }

}
