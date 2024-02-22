package com.bistro.module.binance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.module.binance.domain.TickerBaseInfo;
import com.bistro.module.binance.service.IBinanceTickerService;
import com.bistro.module.game.service.ICoinPeriodService;
import com.bistro.web.core.config.CoinConfig;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * https://binance-docs.github.io/apidocs/spot/cn/#symbol-ticker-3
 * {
 * "e": "24hrTicker",  // 事件类型
 * "E": 123456789,     // 事件时间
 * "s": "BNBBTC",      // 交易对
 * "p": "0.0015",      // 24小时价格变化
 * "P": "250.00",      // 24小时价格变化(百分比)
 * "w": "0.0018",      // 平均价格
 * "x": "0.0009",      // 整整24小时之前，向前数的最后一次成交价格
 * "c": "0.0025",      // 最新成交价格
 * "Q": "10",          // 最新成交交易的成交量
 * "b": "0.0024",      // 目前最高买单价
 * "B": "10",          // 目前最高买单价的挂单量
 * "a": "0.0026",      // 目前最低卖单价
 * "A": "100",         // 目前最低卖单价的挂单量
 * "o": "0.0010",      // 整整24小时前，向后数的第一次成交价格
 * "h": "0.0025",      // 24小时内最高成交价
 * "l": "0.0010",      // 24小时内最低成交价
 * "v": "10000",       // 24小时内成交量
 * "q": "18",          // 24小时内成交额
 * "O": 0,             // 统计开始时间
 * "C": 86400000,      // 统计结束时间
 * "F": 0,             // 24小时内第一笔成交交易ID
 * "L": 18150,         // 24小时内最后一笔成交交易ID
 * "n": 18151          // 24小时内成交数
 * }
 */

@Service
public class BinanceTickerServiceImpl implements IBinanceTickerService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceTickerServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CoinConfig coinConfig;

    @Autowired
    private ICoinPeriodService coinPeriodService;

    @Override
    public void addTicker(String symbol, TickerBaseInfo ticker, long time) {
        if (redisTemplate.opsForValue().setIfAbsent(RedisConstants.BINANCE_TICKER_ADD_TAG + symbol + "-" + time, "1", 90, TimeUnit.SECONDS)) {
            //如果有预先设置的价格，就用这个（通常是开奖杀大放小预先设置的值）
            Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().rangeByScoreWithScores(RedisConstants.BINANCE_TICKER_PRESET_PRICE_SET + symbol, time, Double.valueOf(time) + 0.5);
//            logger.info("TypedTuple result:{}", JSONObject.toJSONString(result));
            String preSetPrice = result != null && result.size() > 0 ? result.stream().findFirst().get().getValue() : null;
            if (preSetPrice != null && preSetPrice.startsWith("{")) {
                redisTemplate.opsForZSet().add(RedisConstants.BINANCE_TICKER + symbol, preSetPrice, time);
//                logger.info("addTicker {} preSetPrice:{}", symbol, preSetPrice);
                return;
            }
            //在真实价格随机加减一定的值
            String orginalPrice = JSONObject.toJSONString(ticker);
            double randomPrice = RandomUtils.nextDouble(0.01, 1);
            boolean randomSubtact = RandomUtils.nextBoolean();
            if (randomSubtact) {
                BigDecimal subtractedPrice = new BigDecimal(ticker.getPrice()).subtract(new BigDecimal(randomPrice));
                if (subtractedPrice.compareTo(BigDecimal.ZERO) > 0) {
                    ticker.setPrice(subtractedPrice.setScale(2, RoundingMode.DOWN).toPlainString());
                } else {
//                    logger.info("addTicker {}:{} subtractedPrice小于0:{}", symbol, time, subtractedPrice);
                    ticker.setPrice(new BigDecimal(ticker.getPrice()).setScale(2, RoundingMode.DOWN).toPlainString());
                }
            } else {
                BigDecimal addedPrice = new BigDecimal(ticker.getPrice()).add(new BigDecimal(randomPrice));
                ticker.setPrice(addedPrice.setScale(2, RoundingMode.DOWN).toPlainString());
            }
            String price = JSONObject.toJSONString(ticker);

            redisTemplate.opsForZSet().add(RedisConstants.BINANCE_TICKER + symbol, price, time);
//            logger.info("addTicker {}:{}={}{}{}", symbol, price, orginalPrice, randomSubtact ? "-" : "+", randomPrice);
        }
    }

    @Override
    public JSONObject getTicker(String coin, Long startTime, Integer rows) {
        String symbol = coinConfig.getSymbol().get(coin);
        Set<String> tickerZset = null;
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> xPeriod = null;
        if (startTime == null) {//第1次获取 历史涨跌 当期期号
            tickerZset = redisTemplate.opsForZSet().reverseRangeByScore(RedisConstants.BINANCE_TICKER + symbol, 0, Long.MAX_VALUE, 0, Constants.BINANCE_TICKER_SIZE);
            xPeriod = coinPeriodService.getXPeriod(coin, rows);
            jsonObject.put("periodList", xPeriod);
            jsonObject.put("currentPeriod", coinPeriodService.getCoinPeriod(coin));
        } else {
            startTime = startTime + 1000;//不取出当前startTime score数据
            tickerZset = redisTemplate.opsForZSet().reverseRangeByScore(RedisConstants.BINANCE_TICKER + symbol, startTime, Long.MAX_VALUE);
        }
        Map<Long, JSONObject> timePeriodMap = null;
        if (xPeriod != null) {
            timePeriodMap = xPeriod.stream().collect(Collectors.toMap(s -> s.getLong("drawPriceTime"), s -> s, (value1, value2) -> value1));
        }

        List<TickerBaseInfo> list = new ArrayList<>();

        long time = 0;
        for (String s : tickerZset) {
            TickerBaseInfo ticker = JSONObject.parseObject(s, TickerBaseInfo.class);
            if (timePeriodMap != null) {
                JSONObject timePeriodJson = timePeriodMap.get(ticker.getTime());
                if (timePeriodJson != null) {
                    ticker.setPeriodId(timePeriodJson.getLong("periodId"));
//                    ticker.setUpDown(timePeriodJson.getJSONArray("drawResult").getString(0));
                }
            }
            if (time != ticker.getTime()) {//重复的去掉
                time = ticker.getTime();
                list.add(ticker);
            }
        }
        //按顺序返回
        list = list.stream().sorted(Comparator.comparing(TickerBaseInfo::getTime)).collect(Collectors.toList());
        jsonObject.put("tickerList", list);


        return jsonObject;
    }

    @Override
    public List<TickerBaseInfo> getDrawResult(String coin, Long betEndTime) {
        String symbol = coinConfig.getSymbol().get(coin);
        long start = coinConfig.getDrawTimeContinuityCheck() ?
                (betEndTime - coinConfig.getBetDuration()) * 1000 : 0;//防止数据一直未更新 取最近30秒之内
        long end = (betEndTime + 2) * 1000 - 1;
        Set<String> tickerZset = redisTemplate.opsForZSet().reverseRangeByScore(RedisConstants.BINANCE_TICKER + symbol, start, end, 0, 10);//多去几条数据 防止一秒有重复

        List<TickerBaseInfo> list = new ArrayList<>();

        long time = 0;
        int count = 0;
        for (String s : tickerZset) {
            TickerBaseInfo info = JSONObject.parseObject(s, TickerBaseInfo.class);
            if (time != info.getTime()) {
                time = info.getTime();
                list.add(info);
                count++;
                if (count >= 2) {//取两条就可以了
                    break;
                }
            }
        }
        return list;
    }

    @Override
    public void cleanData(int retainSize, String coin) {
        String symbol = coinConfig.getSymbol().get(coin);
        if (retainSize <= 0) {
            redisTemplate.delete(RedisConstants.BINANCE_TICKER + symbol);
            logger.info("cleanData 全部删除");
        } else {
            Long removeSize = redisTemplate.opsForZSet().removeRange(RedisConstants.BINANCE_TICKER + symbol, 0, 0 - retainSize - 1);
            logger.info("cleanData 删除:{}, 剩余:{}", removeSize, redisTemplate.opsForZSet().count(RedisConstants.BINANCE_TICKER + symbol, 0, Long.MAX_VALUE));
        }

    }

    public static void main(String[] args) {
        System.out.println(RandomUtils.nextDouble(0.5, 10));
    }
}
