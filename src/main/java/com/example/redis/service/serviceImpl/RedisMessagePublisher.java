package com.example.redis.service.serviceImpl;

import com.example.redis.service.MessagePublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisMessagePublisher implements MessagePublisher {
    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic topic;


    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(String message) {
            redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
