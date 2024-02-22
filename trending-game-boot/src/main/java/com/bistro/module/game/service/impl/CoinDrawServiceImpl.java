package com.bistro.module.game.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bistro.common.exception.ServiceException;
import com.bistro.common.utils.DateUtils;
import com.bistro.common.utils.StringUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.message.model.MessageUser;
import com.bistro.module.api.vo.CoinBetRequest;
import com.bistro.module.binance.domain.TickerBaseInfo;
import com.bistro.module.binance.service.IBinanceTickerService;
import com.bistro.module.game.domain.*;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.mapper.GamePayoutMapper;
import com.bistro.module.game.mapper.GamePeriodMapper;
import com.bistro.module.game.mapper.GameRecordInfoMapper;
import com.bistro.module.game.service.ICoinDrawService;
import com.bistro.module.game.service.ICoinPeriodService;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.module.game.service.IUserBalanceService;
import com.bistro.module.payment.service.IPaymentService;
import com.bistro.module.seamless.client.SeamlessBatchPayoutResult;
import com.bistro.module.seamless.client.SeamlessResult;
import com.bistro.web.core.config.CoinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CoinDrawServiceImpl implements ICoinDrawService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinDrawServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IBinanceTickerService tickerService;

    @Autowired
    private GameRecordInfoMapper gameRecordInfoMapper;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private GamePayoutMapper gamePayoutMapper;

    @Autowired
    private GameBetMapper gameBetMapper;

    @Autowired
    private ICoinPeriodService coinPeriodService;

    @Autowired
    private IGameRankService gameRankService;

    @Autowired
    private IUserBalanceService userBalanceService;

    @Autowired
    private GamePeriodMapper gamePeriodMapper;

    @Autowired
    private CoinConfig coinConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoinDrawInfo draw(String coin, Map coinPeriodMap) {

        String betEndTime = (String) coinPeriodMap.get("betEndTime");
        Long periodId = Long.valueOf((String) coinPeriodMap.get("periodId"));
        String drawResultTimeCached = (String) coinPeriodMap.get("drawResultTime");
        List<TickerBaseInfo> drawResult = null;
        if (StringUtils.isEmpty(drawResultTimeCached)) {
            drawResult = getDrawResult(periodId, coin, betEndTime);
            if (drawResult != null && drawResult.size() >= 2) {
                redisTemplate.opsForHash().put(RedisConstants.COIN_PERIOD_HASH + coin, "drawResultTime", JSONObject.toJSONString(drawResult));
            }
        } else {
            drawResult = JSONObject.parseArray(drawResultTimeCached, TickerBaseInfo.class);
        }

        if (drawResult == null || drawResult.size() < 2) {
            LOGGER.error("开奖时最近时间的数据不够两条:{},{},{}", periodId, coin, JSONObject.toJSONString(drawResult));
            throw new ServiceException("开奖失败：开奖时最近时间的数据不够两条");
        }
        String drawPrice = new BigDecimal(drawResult.get(0).getPrice()).setScale(2, RoundingMode.DOWN).toPlainString();
        //计算大小 奇偶 涨跌
//        List<String> drawResultOptionCode = drawResultCalc(drawResult);
        coinPeriodMap.put("drawPrice", drawPrice);//开奖时的价格
//        coinPeriodMap.put("drawResult", JSONObject.toJSONString(drawResultOptionCode));
        coinPeriodMap.put("drawPriceTime", drawResult.get(0).getTime().toString());//开奖结果的时间
//        coinPeriodMap.put("upDownDvalue", new BigDecimal(drawResult.get(0).getPrice())
//                .subtract(new BigDecimal(drawResult.get(1).getPrice())).stripTrailingZeros().toPlainString());//涨跌差值
        //查询该期投注数据
        BetGameRecordQuery query = new BetGameRecordQuery();
        query.setPeriodId(periodId);
        query.setGameCode(Constants.COIN_GAME_CODE);
        query.setSubGameCode(coin);
        query.setStatus(GameEnum.BetStatusEnum.BET_DONE.getStatus());
        List<BetGameRecordVo> gameRecordInfos = gameBetMapper.selectBetRecord(query);
        List<GameBet> updateBets = new ArrayList<>();
        List<GameRecordInfo> updateRecordInfos = new ArrayList<>();
        List<PayoutInfo> payoutInfos = new ArrayList<>();
        Map<String, String> userDrawResult = new HashMap<>();
        BigDecimal betAmountTotal = BigDecimal.ZERO;
        BigDecimal payoutAmountTotal = BigDecimal.ZERO;
        Date now = DateUtils.getNowDate();
        for (BetGameRecordVo recordInfo : gameRecordInfos) {
            betAmountTotal = betAmountTotal.add(recordInfo.getBetAmount());
            JSONObject gameContent = JSONObject.parseObject(recordInfo.getGameContent());
            String currency = recordInfo.getCurrency();
            String username = recordInfo.getUserName();
            String merchantCode = gameContent.getString(Constants.COIN_BET_CONTENT_MERCHANT);
            BigDecimal maxPayoutAmount = gameContent.getBigDecimal(Constants.COIN_BET_CONTENT_MAX_PAYOUT);
            String betPrice = gameContent.getString(Constants.COIN_BET_CONTENT_BET_PRICE);
            JSONArray coinContent = gameContent.getJSONArray(Constants.COIN_BET_CONTENT);
            List<CoinBetRequest.BetContent> betContents = coinContent.toJavaList(CoinBetRequest.BetContent.class);
            BigDecimal payoutAmount = BigDecimal.ZERO;
            List<String> drawResultOptionCode = drawResultCalc(drawResult, betPrice);
            for (CoinBetRequest.BetContent bet : betContents) {
                if (drawResultOptionCode.contains(bet.getOption().name())) {//命中
                    BigDecimal currentOptionPayout = new BigDecimal(bet.getBetAmount())
                            .multiply(new BigDecimal(bet.getMultiplier()))
                            .setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN);
                    payoutAmount = payoutAmount.add(currentOptionPayout);
                    bet.setPayoutAmount(currentOptionPayout.stripTrailingZeros().toPlainString());
                } else {
                    bet.setPayoutAmount("0");
                    //平退款
                    if (drawResultOptionCode.get(0).equals(Constants.COIN_GOTIE)
                            && (bet.getOption().name().equals(CoinGuessOption.GoUp.name()) ||
                            bet.getOption().name().equals(CoinGuessOption.GoDown.name()))) {
                        BigDecimal currentOptionPayout = new BigDecimal(bet.getBetAmount())
                                .multiply(coinConfig.getGoTieRefundMultiplier())
                                .setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN);
                        payoutAmount = payoutAmount.add(currentOptionPayout);
                        bet.setPayoutAmount(currentOptionPayout.stripTrailingZeros().toPlainString());
                    }
                }
            }
            if (payoutAmount.compareTo(maxPayoutAmount) > 0) {//大于maxpayout
                payoutAmount = maxPayoutAmount;
            }
            payoutAmountTotal = payoutAmountTotal.add(payoutAmount);//本期总的payout
            Map userResult = new HashMap();
            userResult.put("payoutAmount", payoutAmount);
            userResult.put("betContent", betContents);
            userResult.put("drawResult", JSONObject.toJSONString(drawResultOptionCode));
            userDrawResult.put(recordInfo.getBetId().toString(), JSONObject.toJSONString(userResult));

            gameContent.put(Constants.COIN_BET_CONTENT, betContents);
            gameContent.put(Constants.COIN_BET_CONTENT_DRAW_RESULT, JSONObject.toJSONString(drawResultOptionCode));

            GameRecordInfo gameRecordInfo = new GameRecordInfo();
            gameRecordInfo.setId(recordInfo.getRecordId());
            gameRecordInfo.setGameContent(JSONObject.toJSONString(gameContent));
            updateRecordInfos.add(gameRecordInfo);

            if (payoutAmount.compareTo(BigDecimal.ZERO) > 0) {
                GameBet gameBet = new GameBet();
                gameBet.setId(recordInfo.getBetId());
                gameBet.setPayoutAmount(payoutAmount);
                gameBet.setMultiplier(gameBet.getPayoutAmount().divide(recordInfo.getBetAmount(), 2, RoundingMode.DOWN));
                gameBet.setStatus(GameEnum.BetStatusEnum.PAY_ING.getStatus());
                gameBet.setCurrency(currency);
                gameBet.setUpdateTime(now);
                gameBet.setGameCode(recordInfo.getGameCode());
                gameBet.setSubGameCode(recordInfo.getSubGameCode());
                gameBet.setPeriodId(recordInfo.getPeriodId());
                gameBet.setUid(recordInfo.getUid());
                gameBet.setBetAmount(recordInfo.getBetAmount());
                updateBets.add(gameBet);

                PayoutInfo payoutInfo = new PayoutInfo();
                payoutInfo.setBet(gameBet);
                MessageUser user = new MessageUser();
                user.setUid(recordInfo.getUid().intValue());
                user.setOpenId(recordInfo.getOpenId());
                user.setUsername(username);
                user.setMerchantCode(merchantCode);
                payoutInfo.setUser(user);
                payoutInfos.add(payoutInfo);

            } else {
                GameBet gameBet = new GameBet();
                gameBet.setId(recordInfo.getBetId());
                gameBet.setPayoutAmount((BigDecimal.ZERO));
                gameBet.setMultiplier(BigDecimal.ZERO);
                gameBet.setStatus(GameEnum.BetStatusEnum.BOMB.getStatus());
                gameBet.setUpdateTime(now);
                gameBet.setGameCode(recordInfo.getGameCode());
                gameBet.setSubGameCode(recordInfo.getSubGameCode());
                gameBet.setPeriodId(recordInfo.getPeriodId());
                gameBet.setUid(recordInfo.getUid());
                gameBet.setBetAmount(recordInfo.getBetAmount());
                updateBets.add(gameBet);
            }
        }
        //更新bet
        if (updateBets != null && updateBets.size() > 0) {
            gameBetMapper.updateBatch(updateBets);
        }
        //更新recordInfo
        if (updateRecordInfos != null && updateRecordInfos.size() > 0) {
            gameRecordInfoMapper.batchUpdateContent(updateRecordInfos);
        }

        //更新奖期
        GamePeriod gamePeriod = new GamePeriod();
//        gamePeriod.setDrawResult(JSONObject.toJSONString(drawResultOptionCode));
        gamePeriod.setDrawValue(drawPrice);
        gamePeriod.setExtInfo(JSONObject.toJSONString(drawResult));
        gamePeriod.setIncome(betAmountTotal.subtract(payoutAmountTotal));
        gamePeriod.setOrderCount(gameRecordInfos != null ? (long) gameRecordInfos.size() : 0L);
        gamePeriod.setId(periodId);
        gamePeriodMapper.updateGamePeriod(gamePeriod);

        CoinDrawInfo coinDrawInfo = new CoinDrawInfo();
        coinDrawInfo.setPayoutInfoList(payoutInfos);
        coinDrawInfo.setCoin(coin);
        coinDrawInfo.setPeriodId(periodId);
        coinDrawInfo.setCoinPeriod(coinPeriodMap);
        coinDrawInfo.setUserDrawResult(userDrawResult);
        coinDrawInfo.setGameBetList(updateBets);
        return coinDrawInfo;
    }

    @Override
    public void updateRedisAfterDraw(CoinDrawInfo drawInfo) {
        //update balance
        userBalanceService.batchAddBalance(drawInfo.getPayoutInfoList());
        //更新总的payout，总的betAmount
        updateTotalAmount(drawInfo.getGameBetList());
        //更新用户结果
        updateRedisDrawResult(drawInfo.getCoin(), drawInfo.getPeriodId(), drawInfo.getUserDrawResult());
        //addPeriodToSet
        coinPeriodService.addPeriodToSet(drawInfo.getCoin(), drawInfo.getPeriodId(), drawInfo.getCoinPeriod());
        //更新排行
        gameRankService.updateCoinRankAfterDraw(drawInfo.getCoin(), drawInfo.getPeriodId(), drawInfo.getGameBetList());
        //更新奖期
        coinPeriodService.updateCurrentPeriod(drawInfo.getCoin(), drawInfo.getCoinPeriod());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeamlessResult payout(GameBet gameBet, MessageUser fromUser) {
        //支付
        //派奖
        SeamlessResult payResult = paymentService.payout(gameBet, fromUser);
        if (payResult.isSuccess()) {
            GamePayout gamePayout = new GamePayout();
            gamePayout.setGameCode(gameBet.getGameCode());
            gamePayout.setBetId(gameBet.getId());
            gamePayout.setUid(Long.valueOf(fromUser.getUid()));
            gamePayout.setAmount(gameBet.getPayoutAmount());
            gamePayout.setMultiplier(gameBet.getMultiplier());
            gamePayout.setOpenId(fromUser.getOpenId());
            gamePayout.setCreateTime(DateUtils.getNowDate());
            gamePayout.setUpdateTime(gamePayout.getCreateTime());
            gamePayout.setSubGameCode(gameBet.getSubGameCode());
            gamePayout.setPeriodId(gameBet.getPeriodId());
            gamePayout.setCurrency(gameBet.getCurrency());
            gamePayoutMapper.insertGamePayout(gamePayout);
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBet.setStatus(GameEnum.BetStatusEnum.WT_DONE.getStatus());
            gameBetMapper.updateGameBet(gameBet);

        } else {

            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBet.setStatus(GameEnum.BetStatusEnum.WT_FAILED.getStatus());
            gameBetMapper.updateGameBet(gameBet);
        }
        return payResult;

    }

    //
    private List<String> drawResultCalc(List<TickerBaseInfo> drawResult, String betPrice) {
        //计算大小 奇偶 涨跌 数字
        List<String> drawResultCalc = new ArrayList<>();
        if (drawResult == null || drawResult.size() == 1) {//异常情况 算用户输
            return drawResultCalc;
        } else {
            TickerBaseInfo lastSec = drawResult.get(0);//31秒

            BigDecimal priceDecimal = new BigDecimal(lastSec.getPrice()).setScale(2, RoundingMode.DOWN);
            String price = priceDecimal.toPlainString();

            //涨跌
            BigDecimal preSecDecimal = betPrice == null ? new BigDecimal(drawResult.get(1).getPrice()).setScale(2, RoundingMode.DOWN) :
                    new BigDecimal(betPrice).setScale(2, RoundingMode.DOWN);
//            String preSec = preSecDecimal.toPlainString();
            if (priceDecimal.compareTo(preSecDecimal) > 0) {
                drawResultCalc.add(CoinGuessOption.GoUp.name());
            } else if (priceDecimal.compareTo(preSecDecimal) < 0) {
                drawResultCalc.add(CoinGuessOption.GoDown.name());
            } else {
                //平
                drawResultCalc.add(Constants.COIN_GOTIE);
            }

            //大小
//            int startIndex = price.indexOf('.');
//            if (startIndex >= 0) {
//                String s = price.substring(startIndex + 2, startIndex + 3);
//                if (Integer.valueOf(s) >= 5) {
//                    drawResultCalc.add(CoinGuessOption.Big.name());
//                } else {
//                    drawResultCalc.add(CoinGuessOption.Small.name());
//                }
//            } else {
//                drawResultCalc.add(CoinGuessOption.Small.name());
//            }

            String lastDigit = StringUtils.right(price, 1);
            String lastDigit1 = StringUtils.right(price, 1);

            //奇偶
            if (betPrice != null) {
                lastDigit = StringUtils.right(priceDecimal.subtract(preSecDecimal).setScale(2, RoundingMode.DOWN).toPlainString(), 2);
            }
            if (Integer.valueOf(lastDigit) % 2 == 0) {
                drawResultCalc.add(CoinGuessOption.DigitEven.name());
            } else {
                drawResultCalc.add(CoinGuessOption.DigitOdd.name());
            }

            //数字
            CoinGuessOption[] options = CoinGuessOption.values();
            for (CoinGuessOption option : options) {
                if (option.name().equals("Digit" + lastDigit1)) {
                    drawResultCalc.add(option.name());
                    break;
                }
            }
            return drawResultCalc;
        }
    }


    @Override
    public JSONObject getDrawResultByBetId(String coin, Long coinPeriodId, Long betId) {
        String redisKey = RedisConstants.getCoinUserDrawResultHashKey(coin, Constants.COIN_GAME_CODE, coinPeriodId);
        return JSONObject.parseObject((String) redisTemplate.opsForHash().get(redisKey, betId.toString()));
    }

    @Override
    public void updateRedisDrawResult(String coin, Long coinPeriodId, Map<String, String> userDrawResult) {
        if (userDrawResult != null && userDrawResult.size() > 0) {
            String redisKey = RedisConstants.getCoinUserDrawResultHashKey(coin, Constants.COIN_GAME_CODE, coinPeriodId);
            redisTemplate.opsForHash().putAll(redisKey, userDrawResult);
        }
    }

    private void updateTotalAmount(List<GameBet> gameBetList) {
        if (CollectionUtils.isEmpty(gameBetList)) {
            return;
        }
        for (GameBet gameBet : gameBetList) {
            if (gameBet.getPayoutAmount().compareTo(BigDecimal.ZERO) > 0) {
                double totalPayout = redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, gameBet.getUid() + "_payout", gameBet.getPayoutAmount().doubleValue());
                double uidTotalPayout = redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, "total_payout", gameBet.getPayoutAmount().doubleValue());
                LOGGER.info("updateTotalAmount:uid:{},bet:{},payout:{},uidTotalPayout:{},totalPayout:{}",
                        gameBet.getUid(), gameBet.getBetAmount(), gameBet.getPayoutAmount(), uidTotalPayout, totalPayout);
            }
            double totalBet = redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, gameBet.getUid() + "_bet", gameBet.getBetAmount().doubleValue());
            double uidTotalBet = redisTemplate.opsForHash().increment(RedisConstants.COIN_TOTAL_AMOUNT_HASH, "total_bet", gameBet.getBetAmount().doubleValue());
            LOGGER.info("updateTotalAmount:uid:{},bet:{},payout:{},uidTotalBet:{},totalBet:{}",
                    gameBet.getUid(), gameBet.getBetAmount(), gameBet.getPayoutAmount(), uidTotalBet, totalBet);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeamlessBatchPayoutResult batchPayout(List<PayoutInfo> payoutInfos) {

        //支付
        //派奖
        SeamlessBatchPayoutResult payResult = paymentService.batchPayout(payoutInfos);
        List<GameBet> gameBetList = new ArrayList<>();
        List<GamePayout> gamePayoutList = new ArrayList<>();
        Date now = DateUtils.getNowDate();
        if (payResult.isSuccess()) {
            List<SeamlessBatchPayoutResult.Payout> gamePaymentInfos = payResult.getPayouts();
            Map<String, PayoutInfo> payoutInfoMap = payResult.getPayoutInfos();
            for (SeamlessBatchPayoutResult.Payout payout : gamePaymentInfos) {
                GameBet gameBet = payoutInfoMap.get(payout.getUniqueId()).getBet();
                MessageUser user = payoutInfoMap.get(payout.getUniqueId()).getUser();

                GamePayout gamePayout = new GamePayout();
                gamePayout.setGameCode(gameBet.getGameCode());
                gamePayout.setBetId(gameBet.getId());
                gamePayout.setUid(Long.valueOf(user.getUid()));
                gamePayout.setAmount(gameBet.getPayoutAmount());
                gamePayout.setMultiplier(gameBet.getMultiplier());
                gamePayout.setOpenId(user.getOpenId());
                gamePayout.setCreateTime(DateUtils.getNowDate());
                gamePayout.setUpdateTime(gamePayout.getCreateTime());
                gamePayout.setSubGameCode(gameBet.getSubGameCode());
                gamePayout.setPeriodId(gameBet.getPeriodId());
                gamePayout.setCurrency(gameBet.getCurrency());
                gamePayoutList.add(gamePayout);

                //update gamePayout
                gameBet.setUpdateTime(now);
                if (payout.isSuccess()) {
                    gameBet.setStatus(GameEnum.BetStatusEnum.WT_DONE.getStatus());
                } else {
                    gameBet.setStatus(GameEnum.BetStatusEnum.WT_FAILED.getStatus());
                }
                gameBetList.add(gameBet);
            }

        } else {

            for (PayoutInfo payoutInfo : payoutInfos) {
                GameBet gameBet = payoutInfo.getBet();
                gameBet.setStatus(GameEnum.BetStatusEnum.WT_FAILED.getStatus());
                gameBet.setUpdateTime(now);
                gameBetList.add(gameBet);
            }


        }

        //insert payout
        if (gamePayoutList.size() > 0) {
            gamePayoutMapper.batchInsertGamePayout(gamePayoutList);
        }

        //update gamebet
        gameBetMapper.updateStatusBatch(gameBetList);
        return payResult;
    }

    @Override
    public List<TickerBaseInfo> getDrawResult(Long periodId, String coin, String betEndTime) {
        List<TickerBaseInfo> drawResult = null;
        for (int i = 0; i < 4; i++) {//防止推送数据延迟。最多查询4次
            drawResult = tickerService.getDrawResult(coin, Long.valueOf(betEndTime));
            if (drawResult.get(0).getTime() == (Long.valueOf(betEndTime) + 1) * 1000) {
                return drawResult;
            } else {
                try {
                    Thread.sleep(305);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return drawResult;
    }
}
