package com.bistro.module.task;

import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.module.binance.service.IBinanceTickerService;
import com.bistro.module.game.service.ICoinPeriodService;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.web.core.config.CoinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("cleanDataTask")
public class CleanDataTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanDataTask.class);

    @Autowired
    private IGameRankService gameRankService;

    @Autowired
    private CoinConfig coinConfig;

    @Autowired
    ICoinPeriodService coinPeriodService;

    @Autowired
    private IBinanceTickerService binanceTickerService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public void cleanMinesRankData(Integer retainSize) {
        gameRankService.cleanRankRecord(retainSize, Constants.MINES_GAME_CODE);
    }

    public void cleanCoinTickerData(Integer retainSize) {
        //清除行情数据
        List<String> coins = coinConfig.getSupports();
        for (String coin : coins) {
            binanceTickerService.cleanData(retainSize, coin);
        }
    }

    public void cleanCoinPeriodData(Integer retainSize) {
        //清除奖期信息 保留最近retainSize条
        List<String> coins = coinConfig.getSupports();
        for (String coin : coins) {
            coinPeriodService.cleanHistoryDateRedis(coin, retainSize);
        }
    }

    public void cleanPresetPrice(Integer retainSize) {
        List<String> coins = coinConfig.getSupports();
        for (String coin : coins) {
            Long removeSize = redisTemplate.opsForZSet().removeRange(RedisConstants.BINANCE_TICKER_PRESET_PRICE_SET + coinConfig.getSymbol().get(coin), 0, 0 - retainSize - 1);
            LOGGER.info("cleanPresetPrice size:{}", removeSize);
        }
    }
}
