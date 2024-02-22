package com.bistro.web.core.websocket.client;

import org.springframework.context.ApplicationEvent;

public class WebSocketRetryEvent extends ApplicationEvent {

    public WebSocketRetryEvent(Object source){
        super(source);
    }

}
