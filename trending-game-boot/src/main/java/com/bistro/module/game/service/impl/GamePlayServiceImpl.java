package com.bistro.module.game.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bistro.aop.RedissonLock;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.common.utils.DateUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.message.model.MessageUser;
import com.bistro.message.model.PlayMessage;
import com.bistro.message.model.WithdrawMessage;
import com.bistro.module.api.vo.OngoingGameInfo;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.GameEnum;
import com.bistro.module.game.domain.GameRecodeDetail;
import com.bistro.module.game.domain.GameRecordInfo;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.mapper.GameRecodeDetailMapper;
import com.bistro.module.game.mapper.GameRecordInfoMapper;
import com.bistro.module.game.service.IGamePlayService;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.module.game.service.IWithdrawService;
import com.bistro.utils.BetidHashUtils;
import com.bistro.utils.MultiplierUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GamePlayServiceImpl implements IGamePlayService {

    private static final Logger log = LoggerFactory.getLogger(GamePlayServiceImpl.class);

    @Autowired
    private GameRecordInfoMapper gameRecordInfoMapper;

    @Autowired
    private GameRecodeDetailMapper gameRecodeDetailMapper;

    @Autowired
    private GameBetMapper gameBetMapper;

    @Autowired
    private IWithdrawService withdrawService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private IGameRankService gameRankService;

    @Override
    @RedissonLock(keyParam = "#from.uid + '_60'")
    @Transactional(rollbackFor = Exception.class)
    public PlayMessage.Response play(MessageUser from, PlayMessage.Request request) {
        Long uid = Long.valueOf(from.getUid());
        String openId = from.getOpenId();

        parmCheck(from, request);

        Long betId = BetidHashUtils.betId(request.getBetId());
        GameRecordInfo gameRecordInfo = gameRecordInfoMapper.selectGameRecordInfoByBetId(betId);

        if (gameRecordInfo == null || !gameRecordInfo.getUid().equals(uid)) {
            throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "parma error");
        }


        JSONObject content = JSONObject.parseObject(gameRecordInfo.getGameContent());
        JSONArray indexList = content.getJSONArray(Constants.MINES_INDEX_ARRAY);
        String currency = content.getString(Constants.MINES_BET_CURRENCY);
        JSONObject multiplierMap = content.getJSONObject(Constants.MINES_BET_MULTIPLIER);
        List<Integer> indexes = request.getIndexes();
        List<GameRecodeDetail> gameRecodeDetails = new ArrayList<>();
        Date now = DateUtils.getNowDate();
        boolean xMines = false;//是否扫到雷
        for (int index : indexes) {
            GameRecodeDetail gameRecodeDetail = new GameRecodeDetail();
            gameRecodeDetail.setGameCode(gameRecordInfo.getGameCode());
            gameRecodeDetail.setBetId(betId);
            gameRecodeDetail.setUid(uid);
            gameRecodeDetail.setIndex(index);
            gameRecodeDetail.setOpenId(openId);
            gameRecodeDetail.setCreateTime(now);

            if (indexList.contains(index)) {
                gameRecodeDetail.setShot(0);//雷
                xMines = true;
            } else {
                gameRecodeDetail.setShot(1);//钻石
            }
            gameRecodeDetails.add(gameRecodeDetail);
        }

        gameRecodeDetailMapper.batchInsertGameRecodeDetail(gameRecodeDetails);

        PlayMessage.Response response = new PlayMessage.Response();
        response.setIsPayed(0);
        int hitCount = 0;
        if (!xMines) {//double check
            List<GameRecodeDetail> details = gameRecodeDetailMapper.selectIndexShotList(betId);
            for (GameRecodeDetail item : details) {
                if (item.getShot() == 0) {//雷
                    xMines = true;
                    log.error("bomb!!!!!....{},{}", JSONObject.toJSONString(request), JSONObject.toJSONString(details));
                } else {
                    hitCount++;
                }
            }
        }

        if (!xMines && hitCount > 0) {
            //计算payout
            //直接从record里取
            BigDecimal multiplier = multiplierMap.getBigDecimal(String.valueOf(hitCount));
            log.info("hit:{},multiplier:{}", hitCount, multiplier);
            BigDecimal payoutAmount = MultiplierUtils.calPayout(multiplier,
                    new BigDecimal(content.getString(Constants.MINES_BET_AMOUNT)),
                    new BigDecimal(content.getString(Constants.MINES_MAX_PAYOUT_AMOUNT)));

            response.setWin(1);
            response.setAmount(payoutAmount.stripTrailingZeros().toPlainString());
            response.setCurrency(currency);

            if (request.getType() == 2) {//自动模式 这里提现
                GameBet gameBet = new GameBet();
                gameBet.setCreateTime(gameRecordInfo.getCreateTime());
                gameBet.setBetAmount(content.getBigDecimal(Constants.MINES_BET_AMOUNT));
                gameBet.setCurrency(content.getString(Constants.MINES_BET_CURRENCY));
                gameBet.setUserName(from.getUsername());
                gameBet.setPayoutAmount(payoutAmount);
                gameBet.setMultiplier(multiplier);
                gameBet.setId(betId);
                gameBet.setGameCode(gameRecordInfo.getGameCode());
                WithdrawMessage.Request withdrawRequest = new WithdrawMessage.Request();
                withdrawRequest.setBetId(request.getBetId());
                withdrawRequest.setBet(gameBet);
                withdrawRequest.setType(2);//自动模式
                withdrawRequest.setIndex(indexList.toJavaList(Integer.class));//雷的位置
                try {
                    WithdrawMessage.Response withdrawResult = withdrawService.withdraw(from, withdrawRequest);
                    if (withdrawResult.getIsPayed() == 1) {
                        response.setBalance(withdrawResult.getBalance());
                        response.setIsPayed(1);
                        response.setIndex(indexList.toJavaList(Integer.class));//自动模式直接返回 前提是支付成功，如果没有成功 还可以接着玩，不然还不知道改咋办
                    }
                    return response;
                } catch (Exception e) {
                    log.info("auto withdraw failed:{}", JSONObject.toJSONString(request), e);
                    return response;
                }

            }
//            gameBetMapper.updateGameBet(gameBet);

        } else {//暴雷了
            response.setWin(0);
            response.setAmount(BigDecimal.ZERO.toPlainString());
            response.setIndex(indexList.toJavaList(Integer.class));
            //更新payoutAmount
            GameBet gameBet = new GameBet();
            gameBet.setId(betId);
            gameBet.setPayoutAmount(BigDecimal.ZERO);
            gameBet.setUpdateTime(now);
            gameBet.setStatus(GameEnum.BetStatusEnum.BOMB.getStatus());
            gameBet.setMultiplier(BigDecimal.ZERO);
            gameBetMapper.updateGameBet(gameBet);
            gameBet.setCreateTime(gameRecordInfo.getCreateTime());
            gameBet.setBetAmount(content.getBigDecimal(Constants.MINES_BET_AMOUNT));
            gameBet.setCurrency(content.getString(Constants.MINES_BET_CURRENCY));
            gameBet.setUserName(from.getUsername());
            threadPoolTaskExecutor.execute(() -> redisTemplate.delete(RedisConstants.getKeyOngoingGame(uid, gameRecordInfo.getGameCode())));
            gameRankService.addRankRecord(gameBet, gameRecordInfo.getGameCode());
        }
        return response;

    }

    private void parmCheck(MessageUser from, PlayMessage.Request request) {
        List<Integer> indexes = request.getIndexes();

        if (indexes == null || indexes.size() == 0) {
            throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "parma error");
        }

        if (request.getType() > 2 || request.getType() < 1) {
            throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "parma error");
        }

        for (int index : indexes) {
            if (index < 0 || index > Constants.MINES_BLOCKS - 1) {
                throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "parma error");
            }
        }
    }

    @Override
    public OngoingGameInfo getOngoingGame(Long uid, String gameCode) {
        String redisCacheBetId = redisTemplate.opsForValue().get(RedisConstants.getKeyOngoingGame(uid, gameCode));
        OngoingGameInfo result = new OngoingGameInfo();
        if (StringUtils.isEmpty(redisCacheBetId)) {
            return result;
        } else {
            long betId = Long.valueOf(redisCacheBetId);
            GameBet gameBet = gameBetMapper.selectGameBetById(betId);
            GameRecordInfo gameRecordInfo = gameRecordInfoMapper.selectGameRecordInfoByBetId(betId);
            JSONObject content = JSONObject.parseObject(gameRecordInfo.getGameContent());
            if (gameBet != null && gameBet.getStatus().equals(GameEnum.BetStatusEnum.BET_DONE.getStatus())) {
                List<GameRecodeDetail> details = gameRecodeDetailMapper.selectIndexShotList(betId);
                List<Integer> diamondIndexes = new ArrayList<>();

                result.setCurrency(gameBet.getCurrency());
                result.setBetId(BetidHashUtils.hashId(betId));
                result.setBetAmount(gameBet.getBetAmount());
                result.setMinesCount(content.getJSONArray(Constants.MINES_INDEX_ARRAY).size());
                for (GameRecodeDetail item : details) {
                    if (item.getShot() == 1) {
                        diamondIndexes.add(item.getIndex());
                    }
                }
                if (diamondIndexes.size() == 0) {
                    result.setAmount(gameBet.getBetAmount());
                } else {
                    JSONObject multiplierMap = content.getJSONObject(Constants.MINES_BET_MULTIPLIER);
                    BigDecimal multiplier = multiplierMap.getBigDecimal(String.valueOf(diamondIndexes.size()));
                    BigDecimal payoutAmount = MultiplierUtils.calPayout(multiplier, result.getBetAmount(), content.getBigDecimal(Constants.MINES_MAX_PAYOUT_AMOUNT));
                    result.setAmount(payoutAmount);
                }


                result.setDiamondIndex(diamondIndexes);
            }
        }
        return result;
    }

}
