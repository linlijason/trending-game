package com.bistro.module.game.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bistro.aop.RedissonLock;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.common.utils.DateUtils;
import com.bistro.common.utils.StringUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.message.model.MessageUser;
import com.bistro.module.api.vo.*;
import com.bistro.module.binance.domain.TickerBaseInfo;
import com.bistro.module.binance.service.IBinanceTickerService;
import com.bistro.module.game.domain.*;
import com.bistro.module.game.mapper.GameBaseInfoMapper;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.mapper.GameRecodeDetailMapper;
import com.bistro.module.game.mapper.GameRecordInfoMapper;
import com.bistro.module.game.service.ICoinBetService;
import com.bistro.module.game.service.ICoinPeriodService;
import com.bistro.module.merchant.domain.MerchantInfo;
import com.bistro.module.merchant.service.IMerchantInfoService;
import com.bistro.module.payment.service.IPaymentService;
import com.bistro.module.player.domain.GameUserInfo;
import com.bistro.module.player.mapper.GameUserInfoMapper;
import com.bistro.module.seamless.client.SeamlessResult;
import com.bistro.utils.BetidHashUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CoinBetServiceImpl implements ICoinBetService {


    private static final Logger LOGGER = LoggerFactory.getLogger(CoinBetServiceImpl.class);

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GameBetMapper gameBetMapper;

    @Autowired
    private GameUserInfoMapper gameUserInfoMapper;

    @Autowired
    private GameBaseInfoMapper baseInfoMapper;

    @Autowired
    private GameRecordInfoMapper gameRecordInfoMapper;

    @Autowired
    private IMerchantInfoService merchantInfoService;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private ICoinPeriodService coinPeriodService;

    @Autowired
    private GameRecodeDetailMapper gameRecodeDetailMapper;

    @Autowired
    private IBinanceTickerService tickerService;


    @Override
    @RedissonLock(keyParam = "#user.uid + '_61' + #request.coin")
    @Transactional(rollbackFor = Exception.class)
    public CoinBetResponse bet(ApiUser user, CoinBetRequest request) {

        Long uid = user.getUid();
        String gameCode = Constants.COIN_GAME_CODE;
        String subGameCode = request.getCoin();
        Long periodId = request.getPeriodId();
        Date now = DateUtils.getNowDate();

        String onceLimitKey = RedisConstants.getCoinBetOnceLimit(uid, gameCode + subGameCode, periodId);


        //下注时间判断
        Map coinPeriodMap = coinPeriodService.getCoinPeriod(request.getCoin());//当前奖期
        Long periodIdFromRedis = Long.valueOf((String) coinPeriodMap.get("periodId"));
        if (!periodId.equals(periodIdFromRedis)) {
            throw new ApiException(ApiExceptionMsgEnum.BET_TIME_LIMIT.getCode(), "order time is overdue");
        } else {
            String betStartTime = (String) coinPeriodMap.get("betStartTime");
            String betLastTime = (String) coinPeriodMap.get("betLastTime");
            Long nowUnix = DateUtils.unixTime();
            if (nowUnix < NumberUtils.toLong(betStartTime, 0) ||
                    nowUnix > NumberUtils.toLong(betLastTime, 0)) {
                throw new ApiException(ApiExceptionMsgEnum.BET_TIME_LIMIT.getCode(), "order time is overdue");
            }

        }
        String roundNo = (String) coinPeriodMap.get("roundNo");

        BigDecimal betAmount = new BigDecimal(request.getBetAmount());

        GameBaseInfo gameBaseInfo = baseInfoMapper.selectGameBaseInfoByCode(gameCode);

        if (gameBaseInfo == null) {
            throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
        }

        if (gameBaseInfo.getIsOpen() == 0) {
            throw new ApiException(ApiExceptionMsgEnum.COM_NOT_OPEN_ERROR.getCode(), "game is temporarily closed");
        }

        List<CoinBetRequest.BetContent> betContents = request.getOptions();
        //涨跌 奇偶只能选一个
        boolean isUpDownExisted = false;
        boolean isEvenOddExisted = false;
        for (CoinBetRequest.BetContent betContent : betContents) {

            if (betContent.getOption().equals(CoinGuessOption.GoUp) || betContent.getOption().equals(CoinGuessOption.GoDown)) {
                if (isUpDownExisted) {
                    LOGGER.error("{}:isUpDownExisted", JSONObject.toJSONString(request));
                    throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
                }
                isUpDownExisted = true;
            }

            if (betContent.getOption().equals(CoinGuessOption.DigitEven) || betContent.getOption().equals(CoinGuessOption.DigitOdd)) {
                if (isEvenOddExisted) {
                    LOGGER.error("{}:isEvenOddExisted", JSONObject.toJSONString(request));
                    throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
                }
                isEvenOddExisted = true;
            }

            if (new BigDecimal(betContent.getBetAmount()).compareTo(gameBaseInfo.getMinBetAmount()) < 0) {
                throw new ApiException(ApiExceptionMsgEnum.BET_AMOUNT_MIN_LIMIT.getCode(), "min amount limit");
            }
            if (new BigDecimal(betContent.getBetAmount()).compareTo(gameBaseInfo.getMaxBetAmount()) > 0) {
                throw new ApiException(ApiExceptionMsgEnum.BET_AMOUNT_MAX_LIMIT.getCode(), "max amount limit");
            }
        }

//        if (betAmount.compareTo(gameBaseInfo.getMinBetAmount()) < 0) {
//            throw new ApiException(ApiExceptionMsgEnum.BET_AMOUNT_MIN_LIMIT.getCode(), "min amount limit");
//        }
//
//        if (betAmount.compareTo(gameBaseInfo.getMaxBetAmount()) > 0) {
//            throw new ApiException(ApiExceptionMsgEnum.BET_AMOUNT_MAX_LIMIT.getCode(), "max amount limit");
//        }

        //update request betContent multiplier
        List<CoinGuessOptionConfig> coinGuessOptionConfigs = gameBaseInfo.getCoinGuessOptions();
        Map<CoinGuessOption, BigDecimal> coinGuessOptionConfigsMap = coinGuessOptionConfigs.stream().collect(Collectors.toMap(CoinGuessOptionConfig::getOption, CoinGuessOptionConfig::getOdds));

        betContents.stream().forEach(o ->
                o.setMultiplier(coinGuessOptionConfigsMap.get(o.getOption()).stripTrailingZeros().toPlainString())
        );


        GameUserInfo userInfo = gameUserInfoMapper.selectGameUserInfoById(uid);
        //只能下单一次
        boolean isOnceSuccess = redisTemplate.opsForValue().setIfAbsent(onceLimitKey, "1", 10, TimeUnit.MINUTES);
        if (!isOnceSuccess) {
            throw new ApiException(ApiExceptionMsgEnum.BET_ONCE_LIMIT.getCode(), "one order per period");
        }

        //这里-1 表明取当前秒数据，getDrawResult做了处理+1
        List<TickerBaseInfo> priceList = tickerService.getDrawResult(subGameCode, now.getTime() / 1000 - 1);
        now = new Date(priceList.get(0).getTime());
        //插入bet数据
        GameBet gameBet = new GameBet();
        gameBet.setGameCode(gameCode);
        gameBet.setBetAmount(betAmount);
        gameBet.setStatus(GameEnum.BetStatusEnum.NEW.getStatus());
        gameBet.setCreateTime(now);
        gameBet.setUpdateTime(now);
        gameBet.setPayoutAmount(BigDecimal.ZERO);
        gameBet.setUid(uid);
        gameBet.setCurrency(request.getCurrency());
        gameBet.setOpenId(userInfo.getOpenId());
        gameBet.setUserName(userInfo.getName());
        gameBet.setSubGameCode(request.getCoin());
        gameBet.setPeriodId(request.getPeriodId());
        gameBet.setRoundNo(roundNo);

        gameBetMapper.insertGameBet(gameBet);
        long betId = gameBet.getId();

        //生成游戏记录
        JSONObject content = new JSONObject();
        content.put(Constants.COIN_BET_CONTENT, request.getOptions());
        content.put(Constants.COIN_BET_CONTENT_MERCHANT, userInfo.getFromCode());
        content.put(Constants.COIN_BET_CONTENT_MAX_PAYOUT, gameBaseInfo.getMaxPayoutAmount().stripTrailingZeros().toPlainString());
        content.put(Constants.COIN_BET_CONTENT_BET_PRICE, priceList.get(0).getPrice());
        content.put(Constants.COIN_BET_CONTENT_BET_TIME, priceList.get(0).getTime());

        GameRecordInfo gameRecordInfo = new GameRecordInfo();
        gameRecordInfo.setGameCode(gameCode);
        gameRecordInfo.setBetId(betId);
        gameRecordInfo.setGameContent(content.toJSONString());
        gameRecordInfo.setCreateTime(now);
        gameRecordInfo.setUpdateTime(now);
        gameRecordInfo.setUid(uid);
        gameRecordInfo.setOpenId(userInfo.getOpenId());
        gameRecordInfoMapper.insertGameRecordInfo(gameRecordInfo);

        //游戏详情
        List<GameRecodeDetail> gameRecodeDetailList = new ArrayList<>();
        for (CoinBetRequest.BetContent betContent : betContents) {
            GameRecodeDetail gameRecodeDetail = new GameRecodeDetail();
            gameRecodeDetail.setBetId(betId);
            gameRecodeDetail.setGameCode(gameCode);
            gameRecodeDetail.setUid(uid);
            gameRecodeDetail.setOpenId(userInfo.getOpenId());
            gameRecodeDetail.setCreateTime(now);
            gameRecodeDetail.setIndex(betContent.getOption().ordinal());
            gameRecodeDetail.setShot(new BigDecimal(betContent.getBetAmount()).multiply(new BigDecimal(100)).intValue());
            gameRecodeDetailList.add(gameRecodeDetail);
        }
        gameRecodeDetailMapper.batchInsertGameRecodeDetail(gameRecodeDetailList);


        MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(userInfo.getFromCode());
        //扣款
        MessageUser fromUser = new MessageUser();
        fromUser.setUid(uid.intValue());
        fromUser.setOpenId(userInfo.getOpenId());
        fromUser.setUsername(userInfo.getName());
        fromUser.setMerchantCode(userInfo.getFromCode());
        fromUser.setSignKey(merchantInfo.getSignKey());
        SeamlessResult payResult = paymentService.bet(gameBet, fromUser);
        //扣款成功
        if (payResult.isSuccess()) {
            gameBet.setStatus(GameEnum.BetStatusEnum.BET_DONE.getStatus());
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBetMapper.updateGameBet(gameBet);

            CoinBetResponse response = new CoinBetResponse();
            response.setBetId(BetidHashUtils.hashId(betId));
            response.setIsPayed(1);
            response.setBalance(payResult.getBalance().stripTrailingZeros().toPlainString());
            response.setCurrency(payResult.getCurrency());
            response.setUsername(userInfo.getName());
            response.setBetTime(now.getTime());
            return response;
        } else {
            //扣款失败
            gameBet.setStatus(GameEnum.BetStatusEnum.BET_FAILED.getStatus());
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBetMapper.updateGameBet(gameBet);
            CoinBetResponse response = new CoinBetResponse();
            response.setBetId(BetidHashUtils.hashId(betId));
            response.setIsPayed(0);
            response.setUsername(userInfo.getName());
            response.setErrorMessage(payResult.getOriginalErrMsg());

            return response;
        }
    }

    @Override
    @RedissonLock(keyParam = "#user.uid + '_61' + #request.coin")
    public CoinBetCancelResponse cancel(ApiUser user, CoinBetCancelRequest request) {
        String gameCode = Constants.COIN_GAME_CODE;
        String subGameCode = request.getCoin();
        Long uid = user.getUid();
        Long betId = BetidHashUtils.betId(request.getBetId());
        Long periodId = request.getPeriodId();

        //下注时间判断
        Map coinPeriodMap = coinPeriodService.getCoinPeriod(request.getCoin());//当前奖期
        Long periodIdFromRedis = Long.valueOf((String) coinPeriodMap.get("periodId"));
        if (!periodId.equals(periodIdFromRedis)) {
            throw new ApiException(ApiExceptionMsgEnum.BET_TIME_LIMIT_CANCEL.getCode(), "can not cancel out of time range");
        } else {
            String betStartTime = (String) coinPeriodMap.get("betStartTime");
            String betLastTime = (String) coinPeriodMap.get("betLastTime");
            Long nowUnix = DateUtils.unixTime();
            if (nowUnix < NumberUtils.toLong(betStartTime, 0) ||
                    nowUnix > NumberUtils.toLong(betLastTime, 0)) {
                throw new ApiException(ApiExceptionMsgEnum.BET_TIME_LIMIT_CANCEL.getCode(), "can not cancel out of time range");
            }

        }
        GameUserInfo userInfo = gameUserInfoMapper.selectGameUserInfoById(user.getUid());
        MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(userInfo.getFromCode());
        //扣款
        MessageUser fromUser = new MessageUser();
        fromUser.setUid(user.getUid().intValue());
        fromUser.setOpenId(userInfo.getOpenId());
        fromUser.setUsername(userInfo.getName());
        fromUser.setMerchantCode(userInfo.getFromCode());
        fromUser.setSignKey(merchantInfo.getSignKey());
        GameBet gameBet = gameBetMapper.selectGameBetById(betId);
        if (gameBet == null || !gameBet.getStatus().equals(GameEnum.BetStatusEnum.BET_DONE.getStatus())
                || !gameBet.getUid().equals(user.getUid())) {
            throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
        }
        SeamlessResult payResult = paymentService.refund(gameBet, fromUser, GameEnum.PaymentBusinessTypeEnum.CANCEL);
        //refund成功
        if (payResult.isSuccess()) {
            gameBet.setStatus(GameEnum.BetStatusEnum.CANCEL.getStatus());
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBetMapper.updateGameBet(gameBet);

            CoinBetCancelResponse response = new CoinBetCancelResponse();
            response.setBetId(request.getBetId());
            response.setIsCanceled(1);
            response.setBalance(payResult.getBalance().stripTrailingZeros().toPlainString());
            response.setCurrency(payResult.getCurrency());
            response.setUsername(userInfo.getName());
            response.setBetAmount(gameBet.getBetAmount().toPlainString());
            return response;
        } else {
            CoinBetCancelResponse response = new CoinBetCancelResponse();
            response.setBetId(request.getBetId());
            response.setIsCanceled(0);
            return response;
        }
    }

    @Override
    public void removeOnceLimitKey(Long periodId, String coin, Long uid) {
        String onceLimitKey = RedisConstants.getCoinBetOnceLimit(uid, Constants.COIN_GAME_CODE + coin, periodId);
        redisTemplate.delete(onceLimitKey);
    }

    @Override
    public List<CoinMyBetResponse> selectGameRecordByUidAndCode(ApiUser user, CoinMyBetRequest request) {
        GameBetRecordQuery query = new GameBetRecordQuery();
        query.setGameCode(Constants.COIN_GAME_CODE);
        query.setSubGameCode(request.getCoin());
        query.setRows((request.getRows() == null || request.getRows() > 100) ? 10 : request.getRows());
        query.setUid(user.getUid());
        List<GameBetRecordInfo> result = gameRecordInfoMapper.selectGameRecordByUidAndCode(query);
        List<CoinMyBetResponse> list = new ArrayList<>();
        for (GameBetRecordInfo info : result) {
            CoinMyBetResponse response = new CoinMyBetResponse();
            JSONObject content = JSONObject.parseObject(info.getGameContent());
            response.setPeriodId(info.getPeriodId());
            response.setRoundNo(info.getRoundNo());
            response.setDrawEndTime(info.getDrawTime().atZone(ZoneOffset.ofHours(8)).toEpochSecond());
            response.setDrawPrice(info.getDrawValue());
            response.setBetId(BetidHashUtils.hashId(info.getBetId()));
            response.setBetPrice(content.getString(Constants.COIN_BET_CONTENT_BET_PRICE));
            response.setBetTime(content.getLong(Constants.COIN_BET_CONTENT_BET_TIME));
            if (info.getExtInfo() == null) {
                response.setDrawPriceTime(null);
            } else {
                response.setDrawPriceTime(JSON.parseArray(info.getExtInfo()).getJSONObject(0).getLongValue("time"));
            }
            response.setDrawResult(content.getJSONArray(Constants.COIN_BET_CONTENT).toJavaList(CoinBetRequest.BetContent.class));
            response.setPayoutAmount(StringUtils.isEmpty(info.getDrawValue()) ? "" : info.getPayoutAmount().stripTrailingZeros().toPlainString());
            list.add(response);
        }
        return list;
    }
}
