package com.bistro.web.core.websocket;

import com.bistro.message.WsMessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;

@Component
public class WsMessageTemplateImpl implements WsMessageTemplate {


    private Logger logger = LoggerFactory.getLogger(WsMessageTemplateImpl.class);
    @Override
    public void sendText(String to, String body) {

        Session session =WebSocketServer. sessionMap.get(to);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(body);
                logger.info("回复消息 {} {}", to, body);
            } catch (IOException e) {
                logger.error("发送失败:to={},msg={}", to, body, e);
            }
            return;
        }
        logger.warn("发送失败 用户不在线.to={},msg={}", to, body);
    }
}
