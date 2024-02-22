package com.bistro.module.game.service;

import com.alibaba.fastjson.JSONObject;
import com.bistro.message.model.MessageUser;
import com.bistro.module.binance.domain.TickerBaseInfo;
import com.bistro.module.game.domain.CoinDrawInfo;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.PayoutInfo;
import com.bistro.module.seamless.client.SeamlessBatchPayoutResult;
import com.bistro.module.seamless.client.SeamlessResult;

import java.util.List;
import java.util.Map;

/**
 * 开奖
 */
public interface ICoinDrawService {

    CoinDrawInfo draw(String coin, Map coinPeriodMap);

    void updateRedisAfterDraw(CoinDrawInfo drawInfo);

    SeamlessResult payout(GameBet gameBet, MessageUser fromUser);

    /**
     * 根据betid中奖信息
     * @param betId
     * @return
     */
    JSONObject getDrawResultByBetId(String coin, Long coinPeriodId, Long betId);

    void updateRedisDrawResult(String coin, Long coinPeriodId, Map<String,String> userDrawResult);

    SeamlessBatchPayoutResult batchPayout(List<PayoutInfo> payoutInfos);

    List<TickerBaseInfo> getDrawResult(Long periodId, String coin, String betEndTime);

}
