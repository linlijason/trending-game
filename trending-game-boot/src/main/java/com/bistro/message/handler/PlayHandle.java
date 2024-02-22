package com.bistro.message.handler;

import com.alibaba.fastjson.JSON;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.message.WsMessageTemplate;
import com.bistro.message.model.CommMessage;
import com.bistro.message.model.MessageType;
import com.bistro.message.model.MessageUser;
import com.bistro.message.model.PlayMessage;
import com.bistro.module.game.service.IGamePlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayHandle implements MessageHandle<PlayMessage> {

    private static final Logger log = LoggerFactory.getLogger(PlayHandle.class);
    @Autowired
    private WsMessageTemplate template;

    @Autowired
    private IGamePlayService gamePlayService;

    @Override
    public void handle(MessageUser from, PlayMessage message) {
        PlayMessage.Request request = message.getRequest();
        PlayMessage.Response response = null;
        AjaxResult result = null;
        try {
            response = gamePlayService.play(from, request);
            result = AjaxResult.success(response);
        } catch (ApiException e) {
            log.error("play handle:{},{},{}", JSON.toJSONString(from), JSON.toJSONString(message), e.getMessage());
            result = AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("play handle:{},{}", JSON.toJSONString(from), JSON.toJSONString(message), e);
            result = AjaxResult.error(ApiExceptionMsgEnum.COM_UNKNOWN_ERROR.getCode(), "unknown error");
        }
        template.sendText(from.getSessionId(),
                JSON.toJSONString(CommMessage.create(MessageType.Play, result)));
    }

    @Override
    public boolean support(MessageType type) {
        return type == MessageType.Play;
    }
}
