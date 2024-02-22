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
import com.bistro.message.model.WithdrawMessage;
import com.bistro.module.game.domain.*;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.mapper.GamePayoutMapper;
import com.bistro.module.game.mapper.GameRecodeDetailMapper;
import com.bistro.module.game.mapper.GameRecordInfoMapper;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.module.game.service.IWithdrawService;
import com.bistro.module.payment.service.IPaymentService;
import com.bistro.module.seamless.client.SeamlessResult;
import com.bistro.utils.BetidHashUtils;
import com.bistro.utils.MultiplierUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WithdrawServiceImpl implements IWithdrawService {

    private static final Logger log = LoggerFactory.getLogger(WithdrawServiceImpl.class);
    @Autowired
    private GameRecordInfoMapper gameRecordInfoMapper;

    @Autowired
    private GameRecodeDetailMapper gameRecodeDetailMapper;

    @Autowired
    private GamePayoutMapper gamePayoutMapper;

    @Autowired
    private GameBetMapper gameBetMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private IGameRankService gameRankService;

    @Override
    @RedissonLock(keyParam = "#fromUser.uid + '_60'")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public WithdrawMessage.Response withdraw(MessageUser fromUser, WithdrawMessage.Request request) {
        Long uid = Long.valueOf(fromUser.getUid());
        String openId = fromUser.getOpenId();

        WithdrawMessage.Response response = new WithdrawMessage.Response();
        GameBet gameBet = null;
        Long betId = BetidHashUtils.betId(request.getBetId());

        if (request.getType() == null) {//手动模式
            gameBet = gameBetMapper.selectGameBetById(betId);
        } else if (request.getType() == 2) {//自动模式
            gameBet = request.getBet();
            response.setIndex(request.getIndex());
        }

        //手动模式需要返回雷的位置
        if (request.getType() == null) {//手动的时候没有传type
            if (gameBet == null || !uid.equals(gameBet.getUid()) ||
                    (!gameBet.getStatus().equals(GameEnum.BetStatusEnum.BET_DONE.getStatus())
                            && !gameBet.getStatus().equals(GameEnum.BetStatusEnum.WT_FAILED.getStatus()))) {
                throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
            }
            //计算payout
            boolean xMines = false;
            int hitCount = 0;//砖石数量
            List<GameRecodeDetail> details = gameRecodeDetailMapper.selectIndexShotList(betId);
            if(details==null || details.size()==0){
                throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
            }
            for (GameRecodeDetail item : details) {
                if (item.getShot() == 0) {//雷
                    xMines = true;
                    log.error("bomb!!!!!....{},{}", JSONObject.toJSONString(request), JSONObject.toJSONString(details));
                } else {
                    hitCount++;
                }
            }
            if (!xMines && hitCount > 0) {

                GameRecordInfo gameRecordInfo = gameRecordInfoMapper.selectGameRecordInfoByBetId(betId);
                JSONObject content = JSONObject.parseObject(gameRecordInfo.getGameContent());
                JSONArray indexList = content.getJSONArray(Constants.MINES_INDEX_ARRAY);
                JSONObject multiplierMap = content.getJSONObject(Constants.MINES_BET_MULTIPLIER);

                BigDecimal multiplier = multiplierMap.getBigDecimal(String.valueOf(hitCount));
                log.info("hit:{},multiplier:{}", hitCount, multiplier);

                BigDecimal payoutAmount = MultiplierUtils.calPayout(multiplier,
                        new BigDecimal(content.getString(Constants.MINES_BET_AMOUNT)),
                        new BigDecimal(content.getString(Constants.MINES_MAX_PAYOUT_AMOUNT)));
                gameBet.setPayoutAmount(payoutAmount);
                gameBet.setMultiplier(multiplier);
                response.setIndex(indexList.toJavaList(Integer.class));
            }

        }

        //支付
        //派奖
        SeamlessResult payResult = paymentService.payout(gameBet, fromUser);
        if (payResult.isSuccess()) {
            response.setBalance(payResult.getBalance().stripTrailingZeros().toPlainString());
            response.setCurrency(payResult.getCurrency());
            response.setIsPayed(1);

            GamePayout gamePayout = new GamePayout();
            gamePayout.setGameCode(gameBet.getGameCode());
            gamePayout.setBetId(gameBet.getId());
            gamePayout.setUid(uid);
            gamePayout.setAmount(gameBet.getPayoutAmount());
            gamePayout.setMultiplier(gameBet.getMultiplier());
            gamePayout.setOpenId(openId);
            gamePayout.setCreateTime(DateUtils.getNowDate());
            gamePayout.setUpdateTime(gamePayout.getCreateTime());
            gamePayout.setCurrency(gameBet.getCurrency());
            gamePayoutMapper.insertGamePayout(gamePayout);
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBet.setStatus(GameEnum.BetStatusEnum.WT_DONE.getStatus());
            gameBetMapper.updateGameBet(gameBet);

            //如果遇到一直提现不成功，不能开新局 ，要么继续玩输，要么赢了也可能提现不成功？？？？
            GameBet finalGameBet = gameBet;
            threadPoolTaskExecutor.execute(() -> redisTemplate.delete(RedisConstants.getKeyOngoingGame(uid, finalGameBet.getGameCode())));
            gameRankService.addRankRecord(gameBet, gameBet.getGameCode());
        } else {
            response.setIndex(null);//失败了不返回雷的位置
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBet.setStatus(GameEnum.BetStatusEnum.WT_FAILED.getStatus());
            gameBetMapper.updateGameBet(gameBet);
            response.setIsPayed(0);
        }
        return response;
    }
}

