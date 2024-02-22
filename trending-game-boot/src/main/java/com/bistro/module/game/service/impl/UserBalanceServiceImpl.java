package com.bistro.module.game.service.impl;

import com.bistro.constants.RedisConstants;
import com.bistro.module.game.domain.PayoutInfo;
import com.bistro.module.game.service.IUserBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserBalanceServiceImpl implements IUserBalanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBalanceServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void updateBalance(BigDecimal balance, Long uid) {
        redisTemplate.opsForValue().set(redisKey(uid), balance.toPlainString(), 1, TimeUnit.DAYS);

    }

    @Override
    public BigDecimal addBalance(BigDecimal balance, Long uid) {
        return new BigDecimal(redisTemplate.opsForValue().increment(redisKey(uid), balance.doubleValue()));
    }

    private String redisKey(Long uid) {
        return RedisConstants.USER_BALANCE_CURRENCY + uid;
    }

    @Override
    public void batchAddBalance(List<PayoutInfo> payoutInfoList) {
        for (PayoutInfo payoutInfo : payoutInfoList) {
            redisTemplate.opsForValue().increment(redisKey(payoutInfo.getBet().getUid()), payoutInfo.getBet().getPayoutAmount().doubleValue());
        }

    }

    @Override
    public BigDecimal getBalance(Long uid) {
        String result = redisTemplate.opsForValue().get(redisKey(uid));
        return new BigDecimal(result == null ? "0" : result);
    }
}
