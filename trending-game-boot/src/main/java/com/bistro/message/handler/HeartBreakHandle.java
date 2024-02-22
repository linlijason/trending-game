package com.bistro.message.handler;

import com.alibaba.fastjson.JSON;
import com.bistro.constants.RedisConstants;
import com.bistro.message.WsMessageTemplate;
import com.bistro.message.model.CommMessage;
import com.bistro.message.model.HeartBreakMessage;
import com.bistro.message.model.MessageType;
import com.bistro.message.model.MessageUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HeartBreakHandle implements MessageHandle<HeartBreakMessage> {

    @Autowired
    private WsMessageTemplate template;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void handle(MessageUser from, HeartBreakMessage message) {
        HeartBreakMessage pong = new HeartBreakMessage();
        pong.setPong(message.getPing());
        template.sendText(from.getSessionId(),
                JSON.toJSONString(CommMessage.create(MessageType.HB,
                        pong)));
        redisTemplate.opsForValue().set(RedisConstants.WEBSOCKET_HT + from.getSessionId(), from.getSid(), 45000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean support(MessageType type) {
        return type == MessageType.HB;
    }
}
