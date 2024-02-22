package com.bistro.module.game.service;

import com.bistro.module.api.vo.*;

import java.util.List;

public interface ICoinBetService {

    CoinBetResponse bet(ApiUser user, CoinBetRequest request);

    CoinBetCancelResponse cancel(ApiUser user, CoinBetCancelRequest request);

    void removeOnceLimitKey(Long periodId, String coin, Long uid);

    List<CoinMyBetResponse> selectGameRecordByUidAndCode(ApiUser user, CoinMyBetRequest request);


}
