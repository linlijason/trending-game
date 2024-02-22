package com.bistro.message.handler;

import com.alibaba.fastjson.JSON;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.message.WsMessageTemplate;
import com.bistro.message.model.BetMessage;
import com.bistro.message.model.CommMessage;
import com.bistro.message.model.MessageType;
import com.bistro.message.model.MessageUser;
import com.bistro.module.game.service.IGameBetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BetHandle implements MessageHandle<BetMessage> {
    private static final Logger log = LoggerFactory.getLogger(BetHandle.class);
    @Autowired
    private WsMessageTemplate template;

    @Autowired
    private IGameBetService gameBetService;

    @Override
    public void handle(MessageUser from, BetMessage message) {
        BetMessage.Request request = message.getRequest();
        BetMessage.Response response = null;
        AjaxResult result = null;
        try {
            response = gameBetService.playerBet(from, request);
            if (response.getIsPayed() == 0) {
                if (response.getErrorMessage() != null) {
                    result = AjaxResult.error(ApiExceptionMsgEnum.COM_SEAMLESS_ERROR.getCode(), response.getErrorMessage());
                } else {
                    result = AjaxResult.error(ApiExceptionMsgEnum.BET_PAY_ERROR.getCode(), "pay error");
                }
            } else {
                result = AjaxResult.success(response);
            }
        } catch (ApiException e) {
            log.error("bet handle:{},{},{}", JSON.toJSONString(from), JSON.toJSONString(message), e.getMessage());
            result = AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("bet handle:{},{}", JSON.toJSONString(from), JSON.toJSONString(message), e);
            result = AjaxResult.error(ApiExceptionMsgEnum.COM_UNKNOWN_ERROR.getCode(), "unknown error");
        }
        template.sendText(from.getSessionId(),
                JSON.toJSONString(CommMessage.create(MessageType.Bet, result)));
    }

    @Override
    public boolean support(MessageType type) {
        return type == MessageType.Bet;
    }
}
