package com.bistro.web.core.websocket;

import com.alibaba.fastjson.JSON;
import com.bistro.common.utils.spring.SpringUtils;
import com.bistro.constants.RedisConstants;
import com.bistro.framework.manager.AsyncManager;
import com.bistro.message.handler.MessageHandle;
import com.bistro.message.handler.MessageHandleWrapper;
import com.bistro.message.model.CommMessage;
import com.bistro.message.model.MessageUser;
import com.bistro.module.merchant.domain.MerchantInfo;
import com.bistro.module.merchant.service.IMerchantInfoService;
import com.bistro.module.player.domain.GameUserInfo;
import com.bistro.module.player.service.IGameUserInfoService;
import com.bistro.utils.SidUtils;
import com.bistro.utils.UidHashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(value = "/ws/{id}", configurator = WebSocketConfigurator.class)
@Component
@ConditionalOnProperty(value = "websocket.enable", havingValue = "true")
public class WebSocketServer {

    private Logger logger = LoggerFactory.getLogger("websocket");

    static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

    static Map<String, MessageUser> userMap = new ConcurrentHashMap<String, MessageUser>();

    //连接
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "id") String id) throws Exception {

        try {
            String sid = session.getRequestParameterMap().get("sid").get(0);
            Long uid = null;

            if (SidUtils.check(id, sid)) {
                RedisTemplate redisTemplate = SpringUtils.getBean("redisTemplate");
                boolean isNotConnected = redisTemplate.opsForValue().setIfAbsent(RedisConstants.WEBSOCKET_HT + id, sid, 45000, TimeUnit.MILLISECONDS);
                if (!isNotConnected) {
                    logger.info("重复连接：{},{}", id, sid);
                    session.close(new CloseReason(() -> 4003, "game was opened in another tab"));
                    return;
                }
                uid = UidHashUtils.uid(id);
                IGameUserInfoService gameUserInfoService = SpringUtils.getBean(IGameUserInfoService.class);
                IMerchantInfoService merchantInfoService = SpringUtils.getBean(IMerchantInfoService.class);
                GameUserInfo gameUserInfo = gameUserInfoService.selectGameUserInfoById(uid);
                MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(gameUserInfo.getFromCode());
                MessageUser messageUser = new MessageUser();
                messageUser.setOpenId(gameUserInfo.getOpenId());
                messageUser.setUid(gameUserInfo.getId().intValue());
                messageUser.setMerchantCode(gameUserInfo.getFromCode());
                messageUser.setSignKey(merchantInfo.getSignKey());
                messageUser.setUsername(gameUserInfo.getName());
                messageUser.setSessionId(id);
                messageUser.setSid(sid);
                userMap.put(id, messageUser);
                sessionMap.put(id, session);
                logger.info("WEBSOCKET id={},uid={},sid={}连接上服务器", id, uid, sid);

            } else {
                session.close(new CloseReason(() -> 4002, "param error"));
                logger.info("WEBSOCKET id={},uid={},sid={}连接错误", id, uid, sid);
            }
        } catch (Exception e) {
            logger.error("WEBSOCKET id={} 连接错误", id);
            session.close(new CloseReason(() -> 4001, "unknown error"));
        }

    }


    //关闭
    @OnClose
    public void onClose(Session session, @PathParam(value = "id") String id) {
        String sid = session.getRequestParameterMap().get("sid").get(0);
        RedisTemplate redisTemplate = SpringUtils.getBean("redisTemplate");
        if (sid.equals(redisTemplate.opsForValue().get(RedisConstants.WEBSOCKET_HT + id))) {
            sessionMap.remove(id);
            userMap.remove(id);
            redisTemplate.delete(RedisConstants.WEBSOCKET_HT + id);
        }
        logger.info("WEBSOCKET [{},{}]退出了连接", id, sid);
    }

    //接收消息   客户端发送过来的消息
    @OnMessage
    public void onMessage(String message, Session session, @PathParam(value = "id") String id) {
        logger.info("收到消息 {} {}", id, message);
        AsyncManager.me().execute(() -> {
            try {
                CommMessage commMessage = JSON.parseObject(message, CommMessage.class);
                MessageHandleWrapper handleWrapper = SpringUtils.getBean(MessageHandleWrapper.class);
                List<MessageHandle> handles = handleWrapper.getHandles();
                for (MessageHandle handle : handles) {
                    if (handle.support(commMessage.getType())) {
                        Type bodyType = handleWrapper.getBodyType(handle);
                        Object body = commMessage.getBody().toJavaObject(bodyType);
                        handle.handle(userMap.get(id), body);
                    }
                }
            } catch (Exception e) {
                logger.error("onMessage error,id={} message={}", id, message, e);
            }
        });

    }

    //异常
    @OnError
    public void onError(Session session, Throwable throwable, @PathParam(value = "id") String id) {
        logger.error("WEBSOCKET {} 发生异常!", id, throwable);
        try {
            session.close(new CloseReason(() -> 4000, "unknown error"));
        } catch (Exception e) {
            logger.error("WEBSOCKET {} 发生异常session.close!", id, e);
        }

    }


}
