package com.example.redis.service.serviceImpl;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.ArrayList;
import java.util.List;

public class RedisMessageSubscriber implements MessageListener {

    public static List<String> messagesReceived = new ArrayList<String>();
    @Override
    public void onMessage(Message message, byte[] pattern) {
        messagesReceived.add(message.toString());
        System.out.println("Message received: " + message.toString());

    }
}
