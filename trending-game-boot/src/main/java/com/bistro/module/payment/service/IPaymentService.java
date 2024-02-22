package com.bistro.module.payment.service;

import com.bistro.message.model.MessageUser;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.GameEnum;
import com.bistro.module.game.domain.PayoutInfo;
import com.bistro.module.seamless.client.SeamlessBatchPayoutResult;
import com.bistro.module.seamless.client.SeamlessResult;

import java.util.List;

public interface IPaymentService {
    SeamlessResult bet(GameBet gameBet, MessageUser user);

    SeamlessResult payout(GameBet gameBet, MessageUser user);

    SeamlessResult refund(GameBet gameBet, MessageUser user, GameEnum.PaymentBusinessTypeEnum paymentType);

    SeamlessBatchPayoutResult batchPayout(List<PayoutInfo> payoutInfoList);
}
