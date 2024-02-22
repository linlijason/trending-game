package com.bistro.message.handler;


import com.bistro.message.model.MessageType;
import com.bistro.message.model.MessageUser;

public interface MessageHandle<T> {
    void handle(MessageUser from, T message);

    boolean support(MessageType type);
}
