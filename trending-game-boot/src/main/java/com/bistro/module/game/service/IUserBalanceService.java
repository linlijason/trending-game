package com.bistro.module.game.service;

import com.bistro.module.game.domain.PayoutInfo;

import java.math.BigDecimal;
import java.util.List;

public interface IUserBalanceService {
    /**
     * 直接更新
     * @param balance
     */
    void updateBalance(BigDecimal balance, Long uid);

    /**
     * 增减余额
     * @param balance
     * @return
     */
    BigDecimal addBalance(BigDecimal balance, Long uid);

    /**
     * 批量增加余额
     * @param payoutInfoList
     */
    void batchAddBalance(List<PayoutInfo> payoutInfoList);

    /**
     * 获取用户余额
     * @param uid
     * @return
     */
    BigDecimal getBalance(Long uid);
}
