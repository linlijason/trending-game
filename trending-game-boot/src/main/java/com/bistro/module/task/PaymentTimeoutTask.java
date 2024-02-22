package com.bistro.module.task;

import com.bistro.common.utils.DateUtils;
import com.bistro.message.model.MessageUser;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.GameEnum;
import com.bistro.module.game.service.IGameBetService;
import com.bistro.module.merchant.domain.MerchantInfo;
import com.bistro.module.merchant.service.IMerchantInfoService;
import com.bistro.module.payment.domain.GamePaymentTimeout;
import com.bistro.module.payment.service.IGamePaymentTimeoutService;
import com.bistro.module.payment.service.IPaymentService;
import com.bistro.module.player.domain.GameUserInfo;
import com.bistro.module.player.service.IGameUserInfoService;
import com.bistro.module.seamless.client.SeamlessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("paymentTimeoutTask")
public class PaymentTimeoutTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentTimeoutTask.class);

    @Autowired
    private IGamePaymentTimeoutService gamePaymentTimeoutService;

    @Autowired
    private IGameBetService gameBetService;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IGameUserInfoService gameUserInfoService;

    @Autowired
    private IMerchantInfoService merchantInfoService;


    public void process(Integer betTimeoutBefore, Integer payOutTimeBefore, Integer refundRetryCount, Integer payoutRetryCount) {
        GamePaymentTimeout param = new GamePaymentTimeout();
        param.setProcessStatus(0);
        List<GamePaymentTimeout> gamePaymentTimeoutList = gamePaymentTimeoutService.selectGamePaymentTimeoutList(param);
        Long now = System.currentTimeMillis();
        for (GamePaymentTimeout gamePaymentTimeout : gamePaymentTimeoutList) {
            try {
                GameBet gameBet = gameBetService.selectGameBetById(gamePaymentTimeout.getBetId());
                GameUserInfo gameUserInfo = gameUserInfoService.selectGameUserInfoById(gameBet.getUid());
                MessageUser messageUser = new MessageUser();
                messageUser.setUsername(gameUserInfo.getName());
                messageUser.setMerchantCode(gameUserInfo.getFromCode());
                messageUser.setOpenId(gameUserInfo.getOpenId());
                messageUser.setUid(gameUserInfo.getId().intValue());
                MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(gameUserInfo.getFromCode());
                messageUser.setSignKey(merchantInfo.getSignKey());

                if (gamePaymentTimeout.getPaymentType() == GameEnum.PaymentBusinessTypeEnum.BET.getCode()
                        && now - gamePaymentTimeout.getCreateTime().getTime() >= betTimeoutBefore) {
                    SeamlessResult payResult = paymentService.refund(gameBet, messageUser, GameEnum.PaymentBusinessTypeEnum.REFUND);
                    if (payResult.isSuccess()) {
                        gameBet.setUpdateTime(DateUtils.getNowDate());
                        gameBet.setStatus(GameEnum.BetStatusEnum.CANCEL.getStatus());
                        gameBetService.updateGameBet(gameBet);
                        gamePaymentTimeout.setProcessCount(gamePaymentTimeout.getProcessCount() + 1);
                        gamePaymentTimeout.setProcessStatus(1);
                        gamePaymentTimeoutService.updateGamePaymentTimeout(gamePaymentTimeout);
                    } else {
                        gamePaymentTimeout.setProcessCount(gamePaymentTimeout.getProcessCount() + 1);
                        gamePaymentTimeout.setProcessStatus(gamePaymentTimeout.getProcessCount() >= refundRetryCount ? 2 : 0);
                        gamePaymentTimeoutService.updateGamePaymentTimeout(gamePaymentTimeout);
                    }
                }

                if (gamePaymentTimeout.getPaymentType() == GameEnum.PaymentBusinessTypeEnum.WD.getCode()
                        && now - gamePaymentTimeout.getCreateTime().getTime() >= payOutTimeBefore) {

                    SeamlessResult payResult = paymentService.payout(gameBet, messageUser);
                    if (payResult.isSuccess()) {
                        gameBet.setUpdateTime(DateUtils.getNowDate());
                        gameBet.setStatus(GameEnum.BetStatusEnum.WT_DONE.getStatus());
                        gameBetService.updateGameBet(gameBet);
                        gamePaymentTimeout.setProcessCount(gamePaymentTimeout.getProcessCount() + 1);
                        gamePaymentTimeout.setProcessStatus(1);
                        gamePaymentTimeoutService.updateGamePaymentTimeout(gamePaymentTimeout);
                    } else {
                        gamePaymentTimeout.setProcessCount(gamePaymentTimeout.getProcessCount() + 1);
                        gamePaymentTimeout.setProcessStatus(gamePaymentTimeout.getProcessCount() >= payoutRetryCount ? 2 : 0);
                        gamePaymentTimeoutService.updateGamePaymentTimeout(gamePaymentTimeout);
                    }
                }

            } catch (Exception e) {
                LOGGER.error("timeOut{},error", gamePaymentTimeout.getBetId(), e);
            }
        }

    }

}
