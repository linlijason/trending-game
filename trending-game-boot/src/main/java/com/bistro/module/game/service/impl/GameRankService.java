package com.bistro.module.game.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bistro.common.utils.StringUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.module.api.vo.*;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.GameRecodeDetail;
import com.bistro.module.game.domain.GameRecordInfo;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.mapper.GameRecodeDetailMapper;
import com.bistro.module.game.mapper.GameRecordInfoMapper;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.utils.BetidHashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameRankService implements IGameRankService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameRankService.class);

    @Autowired
    private GameBetMapper gameBetMapper;

    @Autowired
    private GameRecordInfoMapper gameRecordInfoMapper;

    @Autowired
    private GameRecodeDetailMapper gameRecodeDetailMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public List<GamePlayInfo> selectBetHistoryByUid(GamePlayRequest request) {
        return gameBetMapper.selectBetHistoryByUid(request);
    }

    @Override
    public List<GamePlayInfo> selectBetHistory(GamePlayRequest request) {
        return gameBetMapper.selectBetHistory(request);
    }

    @Override
    public GameBetDetail getGameBetDetailByHashBetId(String hashBetId) {
        GameRecordInfo gameRecordInfo = gameRecordInfoMapper.selectGameRecordInfoByBetId(BetidHashUtils.betId(hashBetId));
        List<GameRecodeDetail> gameRecodeDetails = gameRecodeDetailMapper.selectIndexShotList(gameRecordInfo.getBetId());
        List<Integer> clickIndexes = new ArrayList<>();
        for (GameRecodeDetail item : gameRecodeDetails) {
            clickIndexes.add(item.getIndex());
        }
        JSONObject content = JSONObject.parseObject(gameRecordInfo.getGameContent());
        JSONArray indexList = content.getJSONArray(Constants.MINES_INDEX_ARRAY);

        GameBetDetail detail = new GameBetDetail();
        detail.setBetId(hashBetId);
        detail.setMinesIndex(indexList);
        detail.setClickedIndex(clickIndexes);
        return detail;
    }

    @Override
    public void addRankRecord(GamePlayInfo gamePlayInfo, String gameCode) {
        threadPoolTaskExecutor.execute(() -> innerAddRankRecord(gamePlayInfo, gameCode));

    }

    private void innerAddRankRecord(GamePlayInfo gamePlayInfo, String gameCode) {
        String info = JSON.toJSONString(gamePlayInfo);
        //随机毫秒数 尽量让score不重复
        String randomMillSec = StringUtils.leftPad(String.valueOf(new Random().nextInt(1000)), 3, "0");
        String rankAllScore = gamePlayInfo.getPlayTime() + randomMillSec;
        redisTemplate.opsForZSet().add(RedisConstants.RANK_ALL + gameCode, info, new BigDecimal(rankAllScore).doubleValue());
        String payoutScore = null;
        String multiplierScore = null;
        if (gamePlayInfo.getPayoutAmount().compareTo(BigDecimal.ZERO) > 0) {
            Long scoreIncrNo = redisTemplate.opsForValue().increment(RedisConstants.RANK_SCORE_INCR + gameCode);
            String scoreIncr = StringUtils.leftPad(scoreIncrNo.toString(), 9, "0");
            payoutScore = gamePlayInfo.getPayoutAmount().setScale(2, RoundingMode.DOWN).toPlainString() + scoreIncr;
            multiplierScore = gamePlayInfo.getMultiplier().setScale(2, RoundingMode.DOWN).toPlainString() + scoreIncr;
            redisTemplate.opsForZSet().add(RedisConstants.RANK_PAY_OUT + gameCode, info, new BigDecimal(payoutScore).doubleValue());
            redisTemplate.opsForZSet().add(RedisConstants.RANK_MULTIPLIER + gameCode, info, new BigDecimal(multiplierScore).doubleValue());
        }
        LOGGER.info("addRankRecord: {},{},{},{}", info, rankAllScore, payoutScore, multiplierScore);
    }

    @Override
    public List<GamePlayInfo> selectRank(GamePlayRequest request) {
        //1 -按payout 从高到第， 2-按multiplier从高到低  3- 我的游戏记录 4- 所有游戏记录
        int rows = request.getRows();
        double min = 0;
        if (!StringUtils.isEmpty(request.getQueryStart())) {
            rows = 51;//最多取50条 去掉min score那条
            min = new BigDecimal(request.getQueryStart()).doubleValue();
        }
        String gameCode = request.getGameCode();

        if (request.getType() == 1) {
            return setToList(redisTemplate.opsForZSet().reverseRangeByScoreWithScores(RedisConstants.RANK_PAY_OUT + gameCode, min, Long.MAX_VALUE, 0, rows), request.getQueryStart());
        } else if (request.getType() == 2) {
            return setToList(redisTemplate.opsForZSet().reverseRangeByScoreWithScores(RedisConstants.RANK_MULTIPLIER + gameCode, min, Long.MAX_VALUE, 0, rows), request.getQueryStart());
        } else if (request.getType() == 4) {
            return setToList(redisTemplate.opsForZSet().reverseRangeByScoreWithScores(RedisConstants.RANK_ALL + gameCode, min, Long.MAX_VALUE, 0, rows), request.getQueryStart());
        }
        return new ArrayList<>();
    }

    private List<GamePlayInfo> setToList(Set<ZSetOperations.TypedTuple<String>> set, String queryStart) {
        return set.stream().map(item -> {
            GamePlayInfo gamePlayInfo = JSONObject.parseObject(item.getValue(), GamePlayInfo.class);
            gamePlayInfo.setQueryStart(new BigDecimal(item.getScore()).setScale(15, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
            return gamePlayInfo;
        }).filter(item -> {
            if (StringUtils.isEmpty(queryStart) || !queryStart.equals(item.getQueryStart())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
    }


    /**
     * @param size 保留的数据量
     */
    @Override
    public void cleanRankRecord(int size, String gameCode) {
        if (size <= 0) {
            redisTemplate.delete(RedisConstants.RANK_ALL + gameCode);
            redisTemplate.delete(RedisConstants.RANK_PAY_OUT + gameCode);
            redisTemplate.delete(RedisConstants.RANK_MULTIPLIER + gameCode);
            LOGGER.info("cleanRankRecord 全部删除");
        } else {
            long removeSize = redisTemplate.opsForZSet().removeRange(RedisConstants.RANK_ALL + gameCode, 0, 0 - size - 1);
            redisTemplate.opsForZSet().removeRange(RedisConstants.RANK_PAY_OUT + gameCode, 0, 0 - size - 1);
            redisTemplate.opsForZSet().removeRange(RedisConstants.RANK_MULTIPLIER + gameCode, 0, 0 - size - 1);
            LOGGER.info("cleanRankRecord 删除:{}, 剩余:{}", removeSize, redisTemplate.opsForZSet().count(RedisConstants.RANK_ALL + gameCode, 0, Long.MAX_VALUE));
        }

    }

    @Override
    public void addRankRecord(GameBet gameBet, String gameCode) {
        GamePlayInfo gamePlayInfo = new GamePlayInfo();
        gamePlayInfo.setBetId(BetidHashUtils.hashId(gameBet.getId()));
        gamePlayInfo.setBetAmount(gameBet.getBetAmount());
        gamePlayInfo.setCurrency(gameBet.getCurrency());
        gamePlayInfo.setPlayer(gameBet.getUserName());
        gamePlayInfo.setPlayTime(String.valueOf(gameBet.getCreateTime().getTime() / 1000));
        gamePlayInfo.setMultiplier(gameBet.getMultiplier());
        gamePlayInfo.setPayoutAmount(gameBet.getPayoutAmount());
        addRankRecord(gamePlayInfo, gameCode);
    }

    @Override
    public void addCoinRankBeforeDraw(Long betId, String coinCode, Long periodId, List<CoinBetRequest.BetContent> contents, BigDecimal betAmount, String user) {

        CoinRankInfo coinRankInfo = new CoinRankInfo();
        coinRankInfo.setOptions(contents.stream().map(i -> i.getOption()).collect(Collectors.toList()));
        coinRankInfo.setUsername(user);
        coinRankInfo.setBetAmount(betAmount.stripTrailingZeros().toPlainString());
        coinRankInfo.setBetId(BetidHashUtils.hashId(betId));
        String redisKey = RedisConstants.getCoinRankKey(coinCode, Constants.COIN_GAME_CODE, periodId);

        redisTemplate.opsForZSet().add(redisKey, JSONObject.toJSONString(coinRankInfo), betAmount.doubleValue());

    }

    @Override
    public void updateCoinRankAfterDraw(String coinCode, Long periodId, List<GameBet> gameBetList) {
        if (gameBetList == null || gameBetList.size() <= 0) {
            return;
        }
        Map<Long, BigDecimal> payoutMap = gameBetList.stream().collect(Collectors.toMap(GameBet::getId, GameBet::getPayoutAmount));
        String redisKey = RedisConstants.getCoinRankKey(coinCode, Constants.COIN_GAME_CODE, periodId);
        Set<String> set = redisTemplate.opsForZSet().rangeByScore(redisKey, 0, Long.MAX_VALUE);
        Set<ZSetOperations.TypedTuple<String>> updateSet = new HashSet<>();
        for (String s : set) {
            CoinRankInfo coinRankInfo = JSONObject.parseObject(s, CoinRankInfo.class);
            BigDecimal payoutAmount = payoutMap.get(BetidHashUtils.betId(coinRankInfo.getBetId()));
            payoutAmount = payoutAmount == null ? BigDecimal.ZERO : payoutAmount;
            coinRankInfo.setPayoutAmount(payoutAmount.stripTrailingZeros().toPlainString());
            ZSetOperations.TypedTuple<String> tuple = new DefaultTypedTuple(JSONObject.toJSONString(coinRankInfo), payoutAmount.doubleValue());
            updateSet.add(tuple);
        }
        //先删除再更新
        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.delete(redisKey);
                operations.opsForZSet().add(redisKey, updateSet);
                return operations.exec();
            }
        };
        redisTemplate.execute(callback);
    }

    @Override
    public List<CoinRankInfo> selectCoinRank(CoinRankRequest request) {
        String redisKey = RedisConstants.getCoinRankKey(request.getCoin(), Constants.COIN_GAME_CODE, request.getPeriodId());
        Set<String> set = redisTemplate.opsForZSet().reverseRangeByScore(redisKey, 0, Long.MAX_VALUE);
        return set.stream().map(s -> JSONObject.parseObject(s, CoinRankInfo.class)).collect(Collectors.toList());
    }

    @Override
    public void removeCoinRankBeforeDraw(Long betId, String coinCode, Long periodId, List<CoinBetRequest.BetContent> contents, BigDecimal betAmount, String user) {
        CoinRankInfo coinRankInfo = new CoinRankInfo();
        coinRankInfo.setOptions(contents.stream().map(i -> i.getOption()).collect(Collectors.toList()));
        coinRankInfo.setUsername(user);
        coinRankInfo.setBetAmount(betAmount.stripTrailingZeros().toPlainString());
        coinRankInfo.setBetId(BetidHashUtils.hashId(betId));
        String redisKey = RedisConstants.getCoinRankKey(coinCode, Constants.COIN_GAME_CODE, periodId);

        redisTemplate.opsForZSet().remove(redisKey, JSONObject.toJSONString(coinRankInfo));
    }
}
