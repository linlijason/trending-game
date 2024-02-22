package com.bistro.web.core.websocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(-2)
@Profile("!dev")
public class WebSocketRunner implements ApplicationRunner {

    private static Logger logger = LoggerFactory.getLogger(WebSocketRunner.class);

    @Autowired
    WebSocketUtils webSocketUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.info("IOC 容器启动, 启动webSocket 连接");
        webSocketUtils.startConnection();
    }
}
