package com.bistro.module.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bistro.common.utils.DateUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.framework.manager.AsyncManager;
import com.bistro.module.api.vo.CoinBetRequest;
import com.bistro.module.binance.domain.TickerBaseInfo;
import com.bistro.module.game.domain.*;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.service.ICoinDrawService;
import com.bistro.module.game.service.ICoinPeriodService;
import com.bistro.module.game.service.IUserBalanceService;
import com.bistro.module.seamless.client.SeamlessBatchPayoutResult;
import com.bistro.module.seamless.client.SeamlessResult;
import com.bistro.web.core.config.CoinConfig;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * coin开奖
 */
@Component("coinDrawTask")
@Profile("!dev")
public class CoinDrawTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoinDrawTask.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CoinConfig coinConfig;


    @Autowired
    ICoinPeriodService coinPeriodService;

    @Autowired
    private ICoinDrawService coinDrawService;

    @Autowired
    private IUserBalanceService userBalanceService;

    @Autowired
    private GameBetMapper gameBetMapper;

    //先查找当前奖期
    //如果不存在 创建奖期
    //如果存在判断 开奖时间
    //到了开奖时间 开奖
    @Scheduled(cron = "0/1 * * * * ?")
    public void draw() {
        boolean isGetLocked = redisTemplate.opsForValue().setIfAbsent(RedisConstants.COIN_DRAW_KEY_RUNNING_JOB, "1", 500, TimeUnit.MILLISECONDS);
        if (!isGetLocked) {
            return;
        }
        List<String> supportCoins = coinConfig.getSupports();
        for (String supportCoin : supportCoins) {
            AsyncManager.me().execute(() -> {
                Map coinPeriod = coinPeriodService.getCoinPeriod(supportCoin);
                if (coinPeriod == null || coinPeriod.size() <= 0) {
                    coinPeriodService.createCoinPeriod(supportCoin, DateUtils.unixTime());
                } else {
                    String drawStartTime = (String) coinPeriod.get("drawStartTime");
                    Long drawEndTime = Long.valueOf((String) coinPeriod.get("drawEndTime"));
                    String periodId = (String) coinPeriod.get("periodId");
                    long now = DateUtils.unixTime();
                    if (now >= Long.valueOf(drawStartTime)) {//开始开奖
                        String tagRedisKey = RedisConstants.getCoinPeriodDrawTag(supportCoin, Constants.COIN_GAME_CODE, Long.valueOf((String) coinPeriod.get("periodId")));
                        boolean canDraw = redisTemplate.opsForValue().setIfAbsent(tagRedisKey, "1", 30000, TimeUnit.MILLISECONDS);
                        if (!canDraw) {
                            LOGGER.info("drawTask: {},{} is drawing", supportCoin, periodId);
                            return;
                        }
                        CoinDrawInfo coinDrawInfo = null;
                        boolean isUpdateDbDone = false;
                        try {
                            LOGGER.info("drawTask:start draw:{}", periodId);
                            coinDrawInfo = coinDrawService.draw(supportCoin, coinPeriod);
                            isUpdateDbDone = true;
                            LOGGER.info("drawTask:after draw:coinDrawInfo:{}", JSONObject.toJSONString(coinDrawInfo));
                        } catch (Exception e) {
                            redisTemplate.delete(tagRedisKey);
                            LOGGER.error("coinDrawService draw error:{},{}", supportCoin, coinPeriod, e);
                        }

                        if (isUpdateDbDone) {
                            //更新redis
                            coinDrawService.updateRedisAfterDraw(coinDrawInfo);
                            LOGGER.info("drawTask:done updateRedisAfterDraw:{}", periodId);
                            //创建新的奖期
                            coinPeriodService.createCoinPeriod(supportCoin, Math.max(DateUtils.unixTime(), drawEndTime + 1));
                            LOGGER.info("drawTask:{},done after create new period", periodId);
                            //payout
                            List<PayoutInfo> payoutInfos = coinDrawInfo.getPayoutInfoList();
                            if (payoutInfos != null && payoutInfos.size() > 0) {
                                if (coinConfig.getBatchPayout()) {
                                    batchPayout(payoutInfos);
                                } else {
                                    payout(payoutInfos);
                                }
                            }
                        }

                    } else {//否则等下一次

                    }
                }
            });
        }
    }

    private void payout(List<PayoutInfo> payoutInfos) {
        for (PayoutInfo payoutInfo : payoutInfos) {
            AsyncManager.me().execute(() -> {
                String payoutStr = JSONObject.toJSONString(payoutInfo);
                LOGGER.info("drawTask:payout:{}", payoutStr);
                SeamlessResult payResult = null;
                try {
                    payResult = coinDrawService.payout(payoutInfo.getBet(), payoutInfo.getUser());
                    //payout成功更新balance
                    if (payResult.getBalance() != null) {
                        userBalanceService.updateBalance(payResult.getBalance(), (long) payoutInfo.getUser().getUid());
                    }
                } catch (Exception e) {
                    LOGGER.error("drawTask:payout error:{}", payoutStr, e);
                }
            });
        }

    }

    private void batchPayout(List<PayoutInfo> payoutInfos) {


        //分批处理
        List<List<PayoutInfo>> groupPayoutInfoList = ListUtils.partition(payoutInfos, coinConfig.getBatchPayoutGroupSize());
        for (List<PayoutInfo> groupPayoutInfo : groupPayoutInfoList) {
            String payoutStr = JSONObject.toJSONString(groupPayoutInfo);
            LOGGER.info("drawTask:payout:{}", payoutStr);
            AsyncManager.me().execute(() -> {
                try {
                    SeamlessBatchPayoutResult payResult = coinDrawService.batchPayout(groupPayoutInfo);
                    if (payResult.isSuccess()) {
                        List<SeamlessBatchPayoutResult.Payout> payouts = payResult.getPayouts();
                        Map<String, PayoutInfo> payoutInfoMap = payResult.getPayoutInfos();
                        for (SeamlessBatchPayoutResult.Payout payout : payouts) {
                            if (payout.getBalance() != null) {
                                userBalanceService.updateBalance(new BigDecimal(payout.getBalance()), (long) payoutInfoMap.get(payout.getUniqueId()).getUser().getUid());
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("drawTask:payout error:{}", payoutStr, e);
                }
            });
        }
    }

    public void presetPrice() {

        List<String> supportCoins = coinConfig.getSupports();
        for (String supportCoin : supportCoins) {
            Map coinPeriod = coinPeriodService.getCoinPeriod(supportCoin);
            if (coinPeriod == null || coinPeriod.size() <= 0) {
                LOGGER.info("{} map is null", supportCoin);
                continue;
            } else {
                Long betLastTime = Long.valueOf((String) coinPeriod.get("betLastTime"));
                if (DateUtils.unixTime() > betLastTime) {
                    String periodId = (String) coinPeriod.get("periodId");
                    String betEndTime = (String) coinPeriod.get("betEndTime");
                    Long presetTime = (Long.valueOf(betEndTime) + 1) * 1000;//转换成毫秒


                    LOGGER.info("presetPrice start:{},periodId={},presetTime={},betLastTime={}", supportCoin, periodId, presetTime, betLastTime);

                    String presetPriceKey = RedisConstants.BINANCE_TICKER_PRESET_PRICE_SET + coinConfig.getSymbol().get(supportCoin);
                    long timeout = coinConfig.getBetDuration() + coinConfig.getDrawDuration();
                    Set<String> existed = redisTemplate.opsForZSet().rangeByScore(presetPriceKey, presetTime, Double.valueOf(presetTime) + 0.5);
                    if (existed != null && existed.size() > 0) {
                        LOGGER.info("presetPrice start:{},periodId={},presetTime={},betLastTime={},timeout={},{}已经设置价格了:{}", supportCoin, periodId, presetTime, betLastTime, timeout, presetPriceKey, existed);
                        continue;
                    }
                    //查询该期投注数据
                    BetGameRecordQuery query = new BetGameRecordQuery();
                    query.setPeriodId(Long.valueOf(periodId));
                    query.setGameCode(Constants.COIN_GAME_CODE);
                    query.setSubGameCode(supportCoin);
                    query.setStatus(GameEnum.BetStatusEnum.BET_DONE.getStatus());
                    List<BetGameRecordVo> gameRecordInfos = gameBetMapper.selectBetRecord(query);
                    BigDecimal upTotalPayAmount = BigDecimal.ZERO;//涨如果中奖金额
                    BigDecimal downTotalPayAmount = BigDecimal.ZERO;//跌如果中奖金额
                    BigDecimal upBetMinPrice = null;//投注涨的最小金额
                    BigDecimal downBetMaxPrice = null;//投注跌的最大金额

                    //只有一个投注时用总的胜率
                    if (gameRecordInfos != null && gameRecordInfos.size() == 1) {
                        List fields = new ArrayList<>();
                        List<Object> amounts = redisTemplate.opsForHash().multiGet(RedisConstants.COIN_TOTAL_AMOUNT_HASH, List.of("total_bet", "total_payout"));
                        BigDecimal bet = new BigDecimal((String) amounts.get(0));
                        BigDecimal payout = new BigDecimal((String) amounts.get(1));
                        LOGGER.info("总的bet和payout=periodId:{},total_bet:{},total_payout:{}", periodId, bet, payout);
                        if (payout.divide(bet, 2, RoundingMode.DOWN).compareTo(new BigDecimal("0.94")) <= 0) {
                            LOGGER.info("periodId:{},只有一个投注时总的胜率小于等于0.94不插针", periodId);
                            redisTemplate.opsForZSet().add(presetPriceKey, presetTime.toString(), Double.valueOf(presetTime));
                            return;
                        }
                    } else {
                        int totalWinLess94 = 0;//小于等于94%个数
                        int totalWinMore94 = 0;//大于94%个数
                        //计算胜率（payout/betAmount）低于94%
                        List fields = new ArrayList<>();
                        for (BetGameRecordVo recordInfo : gameRecordInfos) {
                            fields.add(recordInfo.getUid() + "_bet");
                            fields.add(recordInfo.getUid() + "_payout");
                        }
                        if (fields.size() > 0) {
                            Map<Long, BigDecimal> totalBet = new HashMap<>();
                            Map<Long, BigDecimal> totalPayout = new HashMap<>();
                            List<Object> amounts = redisTemplate.opsForHash().multiGet(RedisConstants.COIN_TOTAL_AMOUNT_HASH, fields);
                            for (int i = 0; i < fields.size(); i++) {
                                String[] amountField = ((String) fields.get(i)).split("_");
                                String uid = amountField[0];
                                String type = amountField[1];
                                String amount = (String) amounts.get(i);
                                if ("bet".equals(type)) {
                                    totalBet.put(Long.valueOf(uid), amount == null ? BigDecimal.ZERO : new BigDecimal(amount));
                                } else {
                                    totalPayout.put(Long.valueOf(uid), amount == null ? BigDecimal.ZERO : new BigDecimal(amount));
                                }
                                LOGGER.info("periodId:{},插针{},amount:{}", periodId, amountField, amount);
                            }

                            for (BetGameRecordVo recordInfo : gameRecordInfos) {
                                BigDecimal bet = totalBet.get(recordInfo.getUid());
                                BigDecimal payout = totalPayout.get(recordInfo.getUid());
                                if (bet == null || payout == null || bet.compareTo(BigDecimal.ZERO) == 0) {
                                    totalWinLess94 = totalWinLess94 + 1;
                                    continue;
                                }
                                if (payout.divide(bet, 2, RoundingMode.DOWN)
                                        .compareTo(new BigDecimal("0.94")) <= 0) {
                                    totalWinLess94 = totalWinLess94 + 1;
                                } else {
                                    totalWinMore94 = totalWinMore94 + 1;
                                }
                            }
                            LOGGER.info("periodId:{},插针totalWinLess94:{},totalWinMore94:{}", periodId, totalWinLess94, totalWinMore94);
                            if (totalWinLess94 > totalWinMore94) {
                                LOGGER.info("totalWinLess94:{} > totalWinLess94:{}不插针", totalWinLess94, totalWinMore94);
                                redisTemplate.opsForZSet().add(presetPriceKey, presetTime.toString(), Double.valueOf(presetTime));
                                return;
                            }
                        }
                    }

                    BigDecimal payOutMax = BigDecimal.ZERO;
                    String upOrDown = "";

                    for (BetGameRecordVo recordInfo : gameRecordInfos) {
                        JSONObject gameContent = JSONObject.parseObject(recordInfo.getGameContent());
                        JSONArray coinContent = gameContent.getJSONArray(Constants.COIN_BET_CONTENT);
                        List<CoinBetRequest.BetContent> betContents = coinContent.toJavaList(CoinBetRequest.BetContent.class);
                        BigDecimal betPrice = gameContent.getBigDecimal(Constants.COIN_BET_CONTENT_BET_PRICE);
                        for (CoinBetRequest.BetContent bet : betContents) {
                            if (CoinGuessOption.GoUp.name().equals(bet.getOption().name())) {//涨
                                BigDecimal currentOptionPayout = new BigDecimal(bet.getBetAmount()).multiply(new BigDecimal(bet.getMultiplier()))
                                        .setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN);
                                upTotalPayAmount = upTotalPayAmount.add(currentOptionPayout);
                                if (currentOptionPayout.compareTo(payOutMax) > 0) {
                                    payOutMax = currentOptionPayout;
                                    upOrDown = "up-" + betPrice.stripTrailingZeros().toPlainString();
                                }
                                if (upBetMinPrice == null) {
                                    upBetMinPrice = betPrice;
                                } else {
                                    if (betPrice.compareTo(upBetMinPrice) < 0) {
                                        upBetMinPrice = betPrice;
                                    }
                                }

                            } else if (CoinGuessOption.GoDown.name().equals(bet.getOption().name())) {//涨
                                BigDecimal currentOptionPayout = new BigDecimal(bet.getBetAmount()).multiply(new BigDecimal(bet.getMultiplier()))
                                        .setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN);
                                downTotalPayAmount = downTotalPayAmount.add(currentOptionPayout);
                                if (currentOptionPayout.compareTo(payOutMax) > 0) {
                                    payOutMax = currentOptionPayout;
                                    upOrDown = "down-" + betPrice.stripTrailingZeros().toPlainString();
                                }
                                if (downBetMaxPrice == null) {
                                    downBetMaxPrice = betPrice;
                                } else {
                                    if (betPrice.compareTo(downBetMaxPrice) > 0) {
                                        downBetMaxPrice = betPrice;
                                    }
                                }

                            }
                        }
                    }
                    LOGGER.info("preset:{},{},upTotalPayAmount={},downTotalPayAmount={},upBetMinPrice={},downBetMaxPrice={},upOrDown={}", supportCoin, periodId, upTotalPayAmount, downTotalPayAmount, upBetMinPrice, downBetMaxPrice, upOrDown);
                    if (downTotalPayAmount.compareTo(upTotalPayAmount) == 0) {
                        if (!upOrDown.equals("")) {
                            LOGGER.info("preset:{},{},downTotalPayAmount和upTotalPayAmount相等:{},upOrDown:{}杀最大投注金额", supportCoin, periodId, upTotalPayAmount, upOrDown);
                            String[] betPrice = upOrDown.split("-");
                            if ("up".equals(betPrice[0])) {
                                goDown(true, presetPriceKey, presetTime, new BigDecimal(betPrice[1]), supportCoin, periodId, upTotalPayAmount);
                            } else {
                                goUp(true, presetPriceKey, presetTime, new BigDecimal(betPrice[1]), supportCoin, periodId);
                            }

                        } else {
                            redisTemplate.opsForZSet().add(presetPriceKey, presetTime.toString(), Double.valueOf(presetTime));
                            LOGGER.info("preset:{},{},downTotalPayAmount和upTotalPayAmount相等", supportCoin, periodId, upTotalPayAmount);
                        }
                    } else if (downTotalPayAmount.compareTo(upTotalPayAmount) < 0) {//跌payout金额小于涨payout金额，那就开跌。 就要保证开奖金额小于投注涨的最小金额，就不可能投注涨的中奖
                        goDown(false, presetPriceKey, presetTime, upBetMinPrice, supportCoin, periodId, upTotalPayAmount);
                    } else {//涨payout金额小于跌payout金额，那就开涨。 就要保证开奖金额大于投注跌的最大金额，就不可能投注跌的中奖
                        goUp(false, presetPriceKey, presetTime, downBetMaxPrice, supportCoin, periodId);
                    }
                }
            }
        }
    }

    private void goDown(boolean usePriceDirectly, String presetPriceKey, long presetTime, BigDecimal upBetMinPrice, String supportCoin, String periodId, BigDecimal upTotalPayAmount) {
        Set<String> tickerZset = redisTemplate.opsForZSet().range(RedisConstants.BINANCE_TICKER + coinConfig.getSymbol().get(supportCoin), -1, -1);
        BigDecimal tempPrice = BigDecimal.ZERO;
        for (String s : tickerZset) {
            TickerBaseInfo ticker = JSONObject.parseObject(s, TickerBaseInfo.class);
            tempPrice = new BigDecimal(ticker.getPrice());
            LOGGER.info("preset:{},tickerZset{}", supportCoin, s);
        }
        if (tempPrice.compareTo(upBetMinPrice) < 0) {
            upBetMinPrice = tempPrice;
        }
        BigDecimal price = upBetMinPrice.subtract(new BigDecimal(RandomUtils.nextDouble(0.51, 2.99)));

        String preSetPrice = price.setScale(2, RoundingMode.DOWN).toPlainString();
        if (!usePriceDirectly && price.compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.info("preset:{},{},price<=0", supportCoin, periodId, upTotalPayAmount);
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        long before = RandomUtils.nextLong(25, 30);
        long after = RandomUtils.nextLong(3, 5);


        BigDecimal diffPrice = price.subtract(tempPrice);
        BigDecimal avgDiffPrice = diffPrice.divide(new BigDecimal(before), 2, RoundingMode.DOWN);
        long halfSize = before / 2;
        long toPriceSize = RandomUtils.nextLong(5, halfSize);
        if (diffPrice.abs().intValue() > 10) {
            toPriceSize = RandomUtils.nextLong(10, 15);
        }

        for (long priceTime = presetTime - before * 1000; priceTime < presetTime + after * 1000; ) {
            priceTime = priceTime + 1000;

            TickerBaseInfo ticker = new TickerBaseInfo();
            if (priceTime == presetTime) {
                ticker.setTime(presetTime);
                ticker.setPrice(preSetPrice);
            } else {
                if (priceTime < presetTime + toPriceSize * 1000) {
                    tempPrice = RandomUtils.nextBoolean() ? tempPrice.add(avgDiffPrice).add(new BigDecimal(RandomUtils.nextDouble(0.01, 1.99))) : price.subtract(new BigDecimal(RandomUtils.nextDouble(0.01, 1.99)));

                } else {
                    tempPrice = RandomUtils.nextBoolean() ? price.add(new BigDecimal(RandomUtils.nextDouble(1, 3.99))) : price.subtract(new BigDecimal(RandomUtils.nextDouble(1, 3.99)));
                }
                ticker.setTime(priceTime);
                if (tempPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                ticker.setPrice(tempPrice.setScale(2, RoundingMode.DOWN).toPlainString());
            }
            tuples.add(new DefaultTypedTuple(JSONObject.toJSONString(ticker), Double.valueOf(priceTime)));
            LOGGER.info("preset:{}", JSONObject.toJSONString(ticker));
        }

        redisTemplate.opsForZSet().add(presetPriceKey, tuples);
        LOGGER.info("preset:{},{},开跌{},{}", supportCoin, periodId, upBetMinPrice, preSetPrice);
    }

    private void goUp(boolean usePriceDirectly, String presetPriceKey, long presetTime, BigDecimal downBetMaxPrice, String supportCoin, String periodId) {
        Set<String> tickerZset = redisTemplate.opsForZSet().range(RedisConstants.BINANCE_TICKER + coinConfig.getSymbol().get(supportCoin), -1, -1);
        BigDecimal tempPrice = BigDecimal.ZERO;
        for (String s : tickerZset) {
            TickerBaseInfo ticker = JSONObject.parseObject(s, TickerBaseInfo.class);
            tempPrice = new BigDecimal(ticker.getPrice());
            LOGGER.info("preset:{},tickerZset{}", supportCoin, s);
        }
        if (!usePriceDirectly && tempPrice.compareTo(downBetMaxPrice) > 0) {
            downBetMaxPrice = tempPrice;
        }
        BigDecimal price = downBetMaxPrice.add(new BigDecimal(RandomUtils.nextDouble(0.51, 2.99)));

        String preSetPrice = price.setScale(2, RoundingMode.DOWN).toPlainString();
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        long before = RandomUtils.nextLong(25, 30);
        long after = RandomUtils.nextLong(3, 5);
        BigDecimal diffPrice = price.subtract(tempPrice);
        BigDecimal avgDiffPrice = diffPrice.divide(new BigDecimal(before), 2, RoundingMode.DOWN);
        long halfSize = before / 2;
        long toPriceSize = RandomUtils.nextLong(5, halfSize);
        if (diffPrice.abs().intValue() > 10) {
            toPriceSize = RandomUtils.nextLong(10, 15);
        }

        for (long priceTime = presetTime - before * 1000; priceTime < presetTime + after * 1000; ) {
            priceTime = priceTime + 1000;
            TickerBaseInfo ticker = new TickerBaseInfo();
            if (priceTime == presetTime) {
                ticker.setTime(presetTime);
                ticker.setPrice(preSetPrice);
            } else {
                if (priceTime < presetTime + toPriceSize * 1000) {
                    tempPrice = RandomUtils.nextBoolean() ? tempPrice.add(avgDiffPrice).add(new BigDecimal(RandomUtils.nextDouble(0.01, 1.99))) : price.subtract(new BigDecimal(RandomUtils.nextDouble(0.01, 1.99)));

                } else {
                    tempPrice = RandomUtils.nextBoolean() ? price.add(new BigDecimal(RandomUtils.nextDouble(1, 3.99))) : price.subtract(new BigDecimal(RandomUtils.nextDouble(1, 3.99)));
                }
                ticker.setTime(priceTime);
                if (tempPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                ticker.setPrice(tempPrice.setScale(2, RoundingMode.DOWN).toPlainString());
            }
            tuples.add(new DefaultTypedTuple(JSONObject.toJSONString(ticker), Double.valueOf(priceTime)));
            LOGGER.info("preset:{}", JSONObject.toJSONString(ticker));
        }
        redisTemplate.opsForZSet().add(presetPriceKey, tuples);
        LOGGER.info("preset:{},{},开涨{},{}", supportCoin, periodId, downBetMaxPrice, preSetPrice);
    }

    public void initCoinTotalAmount() {
        LOGGER.info("initCoinTotalAmount start");
        BetPeriodStatisticsQuery query = new BetPeriodStatisticsQuery();
        query.setGameCode(Constants.COIN_GAME_CODE);
        List<BetPeriodStatisticsVo> statisticsVos = gameBetMapper.selectBetSummaryStatisticsPerUser(query);
        for (BetPeriodStatisticsVo vo : statisticsVos) {
            redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, "total_bet", vo.getBetAmountTotal().doubleValue());
            redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, "total_payout", vo.getPayoutAmountTotal().doubleValue());
            redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, vo.getUid() + "_bet", vo.getBetAmountTotal().doubleValue());
            redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, vo.getUid() + "_payout", vo.getPayoutAmountTotal().doubleValue());
            LOGGER.info("updateTotalAmount:uid:{},bet:{},payout:{}", vo.getUid(), vo.getBetAmountTotal(), vo.getPayoutAmountTotal());
        }
        LOGGER.info("initCoinTotalAmount end:总条数:{}", statisticsVos.size());
    }
}