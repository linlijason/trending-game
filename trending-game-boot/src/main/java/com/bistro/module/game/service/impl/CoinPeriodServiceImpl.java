package com.bistro.module.game.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bistro.common.utils.StringUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.module.binance.domain.TickerBaseInfo;
import com.bistro.module.game.domain.GamePeriod;
import com.bistro.module.game.mapper.GamePeriodMapper;
import com.bistro.module.game.service.ICoinDrawService;
import com.bistro.module.game.service.ICoinPeriodService;
import com.bistro.module.game.service.IUserBalanceService;
import com.bistro.web.core.config.CoinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class CoinPeriodServiceImpl implements ICoinPeriodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoinPeriodServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CoinConfig coinConfig;

    @Autowired
    private ICoinDrawService coinDrawService;

    @Autowired
    private IUserBalanceService userBalanceService;

    @Autowired
    private GamePeriodMapper gamePeriodMapper;

    @Override
    public void createCoinPeriod(String coin, Long time) {

        //double check
        Map latestMap = getCoinPeriod(coin);
        if (latestMap != null && latestMap.size() > 0 && time < Long.valueOf((String) latestMap.get("drawEndTime"))) {
            LOGGER.info("存在奖期了 {}", JSONObject.toJSONString(latestMap));
            return;
        }

        String coinPeriodRoundKey = RedisConstants.COIN_PERIOD_ROUND_NO + coin;
        redisTemplate.opsForValue().setIfAbsent(coinPeriodRoundKey, RedisConstants.COIN_PERIOD_ID_INIT);
        Long coinPeriodRoundNo = redisTemplate.opsForValue().increment(coinPeriodRoundKey);


        int betDuration = coinConfig.getBetDuration();//默认30秒
        int betRealDuration = coinConfig.getBetDurationReal();//默认15秒
        int drawDuration = coinConfig.getDrawDuration();//默认5秒
        Long betEndTime = time + betDuration - 1;
        Long drawEndTime = betEndTime + drawDuration;
        Long betLastTime = time + betRealDuration - 1;//能投注的最后时间
        GamePeriod gamePeriod = new GamePeriod();
        gamePeriod.setGameCode(Constants.COIN_GAME_CODE);
        gamePeriod.setSubGameCode(coin);
        gamePeriod.setBetStartTime(LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8)));
        gamePeriod.setBetEndTime(LocalDateTime.ofEpochSecond(betEndTime, 0, ZoneOffset.ofHours(8)));
        gamePeriod.setDrawTime(LocalDateTime.ofEpochSecond(drawEndTime, 0, ZoneOffset.ofHours(8)));
        gamePeriod.setRoundNo(coinPeriodRoundNo.toString());
        gamePeriodMapper.insertGamePeriod(gamePeriod);
        Long id = gamePeriod.getId();
        Map<String, String> coinPeriodMap = new HashMap<>();

        coinPeriodMap.put("periodId", id.toString());
        coinPeriodMap.put("roundNo", coinPeriodRoundNo.toString());
        coinPeriodMap.put("betDuration", String.valueOf(betDuration));
        coinPeriodMap.put("betRealDuration", String.valueOf(betRealDuration));
        coinPeriodMap.put("drawDuration", String.valueOf(drawDuration));
        coinPeriodMap.put("betStartTime", String.valueOf(time));
        coinPeriodMap.put("betLastTime", String.valueOf(betLastTime));
        coinPeriodMap.put("betEndTime", String.valueOf(betEndTime));
        coinPeriodMap.put("drawStartTime", String.valueOf(time + betDuration + 1));//倒数
        coinPeriodMap.put("drawEndTime", String.valueOf(drawEndTime));//倒数
        coinPeriodMap.put("coin", coin);
        coinPeriodMap.put("drawPrice", "");//开奖时的价格
        coinPeriodMap.put("drawResult", "");
        coinPeriodMap.put("drawResultTime", "");
        coinPeriodMap.put("drawPriceTime", "");//开奖时的价格对应时间

        redisTemplate.opsForHash().putAll(RedisConstants.COIN_PERIOD_HASH + coin, coinPeriodMap);
        LOGGER.info("createCoinPeriod:{},{}", coin, JSONObject.toJSONString(coinPeriodMap));
    }

    @Override
    public Map getCoinPeriod(String coin) {
        return redisTemplate.opsForHash().entries(RedisConstants.COIN_PERIOD_HASH + coin);
    }

    @Override
    public List<JSONObject> getXPeriod(String coin, Integer length) {
        Set<String> result = redisTemplate.opsForZSet().reverseRangeByScore(RedisConstants.COIN_PERIOD_SET + coin, 0, Long.MAX_VALUE, 0, length);
        List<JSONObject> list = new ArrayList<>();
        for (String p : result) {
            JSONObject jsonObject = JSONObject.parseObject(p);
            list.add(jsonObject);
        }
        return list;
    }

    @Override
    public void addPeriodToSet(String coin, Long coinPeriodId, Map coinPeriodMap) {
        redisTemplate.opsForZSet().add(RedisConstants.COIN_PERIOD_SET + coin, JSONObject.toJSONString(coinPeriodMap), coinPeriodId);
    }

    @Override
    public JSONObject getPeriod(String coin, Long coinPeriodId) {
        Set<String> result = redisTemplate.opsForZSet().reverseRangeByScore(RedisConstants.COIN_PERIOD_SET + coin, coinPeriodId, coinPeriodId, 0, 1);
        for (String p : result) {
            return JSONObject.parseObject(p);
        }
        return null;
    }

    @Override
    public JSONObject getPeriod(String coin, Long coinPeriodId, Long betId, Long uid) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonObject = getPeriod(coin, coinPeriodId);
            LOGGER.info("getPeriod:{},{},{},{},{}", coin, coinPeriodId, betId, uid, JSONObject.toJSONString(jsonObject));
            //传了betId并且已开奖才返回信息
            getUserDrawResult(coin, coinPeriodId, uid, betId, jsonObject, result);
            Map latest = getCoinPeriod(coin);
            long latestPeriodId = Long.valueOf((String) latest.get("periodId"));
            if (jsonObject == null) {//未开奖
                if (latestPeriodId == coinPeriodId) {//是同一期
                    latest.put("drawResult", "");//保险起见，即使现在可能已经开奖完成了，但是还是不返回结果
                    String drawResultTimeCached = (String) latest.get("drawResultTime");
                    if (!StringUtils.isEmpty(drawResultTimeCached)) {
                        List<TickerBaseInfo> drawResult = JSONObject.parseArray(drawResultTimeCached, TickerBaseInfo.class);
                        latest.put("drawPrice", drawResult.get(0).getPrice());
                        latest.put("drawPriceTime", drawResult.get(0).getTime());
                    }
                    result.put("currentPeriod", latest);
                    result.put("nextPeriod", latest);
                } else {//新的一期了,表明开奖完了
                    //再取一次
                    jsonObject = getPeriod(coin, coinPeriodId);
                    //传了betId并且已开奖才返回信息
                    getUserDrawResult(coin, coinPeriodId, uid, betId, jsonObject, result);
                    result.put("currentPeriod", jsonObject);
                    result.put("nextPeriod", latest);
                }

            } else {//已开奖
                result.put("currentPeriod", jsonObject);
                result.put("nextPeriod", latest);
            }


        } catch (Exception e) {
            LOGGER.error("getPeriod error:{},{},{},{},", coin, coinPeriodId, betId, uid, e);
        }
        return result;
    }

    @Override
    public void updateCurrentPeriod(String coin, Map periodMap) {
        redisTemplate.opsForHash().putAll(RedisConstants.COIN_PERIOD_HASH + coin, periodMap);
    }

    @Override
    public void cleanHistoryDateRedis(String coin, int retainSize) {

        Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().rangeWithScores(RedisConstants.COIN_PERIOD_SET + coin, 0, 0 - retainSize - 1);

        for (ZSetOperations.TypedTuple<String> s : set) {
            JSONObject item = JSONObject.parseObject(s.getValue());
            Long periodId = item.getLong("periodId");
            //删除用户开奖结果
            redisTemplate.delete(RedisConstants.getCoinUserDrawResultHashKey(coin, Constants.COIN_GAME_CODE, periodId));
            //删除排行
            redisTemplate.delete(RedisConstants.getCoinRankKey(coin, Constants.COIN_GAME_CODE, periodId));
            //从列表删除奖期
            redisTemplate.opsForZSet().remove(RedisConstants.COIN_PERIOD_SET + coin, s.getValue());
        }
        LOGGER.info("cleanHistoryDateRedis 删除:{}, 剩余:{}", set.size(), redisTemplate.opsForZSet().count(RedisConstants.COIN_PERIOD_SET + coin, 0, Long.MAX_VALUE));
    }

    private void getUserDrawResult(String coin, Long coinPeriodId, Long uid, Long betId, JSONObject jsonObject, JSONObject result) {
        if (betId != null && jsonObject != null
                && !StringUtils.isBlank(jsonObject.getString("drawPrice"))) {
            JSONObject resultByBetId = coinDrawService.getDrawResultByBetId(coin, coinPeriodId, betId);
            LOGGER.info("getPeriod getDrawResultByBetId:{},{},{},{},{}", coin, coinPeriodId, betId, uid, resultByBetId.toJSONString());
            result.put("drawResult", resultByBetId.getJSONArray("betContent"));
            result.put("payoutAmount", resultByBetId.getBigDecimal("payoutAmount").stripTrailingZeros().toPlainString());
            result.put("balance", userBalanceService.getBalance(uid).setScale(2, RoundingMode.DOWN).toPlainString());
            jsonObject.put("drawResult", resultByBetId.getString("drawResult"));
        }
    }
}
