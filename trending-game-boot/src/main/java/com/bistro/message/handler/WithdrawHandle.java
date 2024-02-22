package com.bistro.message.handler;

import com.alibaba.fastjson.JSON;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.message.WsMessageTemplate;
import com.bistro.message.model.CommMessage;
import com.bistro.message.model.MessageType;
import com.bistro.message.model.MessageUser;
import com.bistro.message.model.WithdrawMessage;
import com.bistro.module.game.service.IWithdrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WithdrawHandle implements MessageHandle<WithdrawMessage> {
    private static final Logger log = LoggerFactory.getLogger(WithdrawHandle.class);

    @Autowired
    private WsMessageTemplate template;

    @Autowired
    private IWithdrawService withdrawService;

    @Override
    public void handle(MessageUser from, WithdrawMessage message) {
        WithdrawMessage.Request request = message.getRequest();
        request.setType(null);//防止乱传
        AjaxResult result = null;
        try {
            WithdrawMessage.Response response = withdrawService.withdraw(from, request);
            if (response.getIsPayed() == 0) {//支付失败
                throw new ApiException(ApiExceptionMsgEnum.PAYOUT_PAY_ERROR.getCode(), "cash out error");
            }
            result = AjaxResult.success(response);
        } catch (ApiException e) {
            log.error("withdraw error {},{},{}", JSON.toJSONString(from), JSON.toJSONString(message), e.getMessage());
            result = AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("withdraw error {},{}", JSON.toJSONString(from), JSON.toJSONString(message), e);
            result = AjaxResult.error(ApiExceptionMsgEnum.COM_UNKNOWN_ERROR.getCode(), "unknown error");
        }
        template.sendText(from.getSessionId(),
                JSON.toJSONString(CommMessage.create(MessageType.WD, result)));
    }

    @Override
    public boolean support(MessageType type) {
        return type == MessageType.WD;
    }
}
