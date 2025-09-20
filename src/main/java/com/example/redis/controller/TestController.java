package com.example.redis.controller;

import com.example.redis.service.serviceImpl.RedisMessagePublisher;
import com.example.redis.service.serviceImpl.RedisMessageSubscriber;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/test-controller")
public class TestController {
    private final RedisMessagePublisher publisher;
    public TestController(RedisMessagePublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping("/publish")
    public String publishMessage(@RequestParam String message) {
//        String message = "message " + UUID.randomUUID();
        publisher.publish(message);
        return message;

    }

    @GetMapping("/messages")
    public List<String> getMessages() {
        return RedisMessageSubscriber.messagesReceived;
    }
}
