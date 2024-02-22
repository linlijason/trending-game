package com.bistro.module.game.service.impl;

import com.bistro.common.exception.ServiceException;
import com.bistro.common.utils.DateUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.message.model.MessageUser;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.GameEnum;
import com.bistro.module.game.domain.GamePayout;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.mapper.GamePayoutMapper;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.module.game.service.IManualTransactionService;
import com.bistro.module.merchant.domain.MerchantInfo;
import com.bistro.module.merchant.service.IMerchantInfoService;
import com.bistro.module.payment.service.IPaymentService;
import com.bistro.module.player.domain.GameUserInfo;
import com.bistro.module.player.mapper.GameUserInfoMapper;
import com.bistro.module.seamless.client.SeamlessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ManualTransactionServiceImpl implements IManualTransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualTransactionServiceImpl.class);

    @Autowired
    GameBetMapper gameBetMapper;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    GameUserInfoMapper gameUserInfoMapper;

    @Autowired
    GamePayoutMapper gamePayoutMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private IGameRankService gameRankService;

    @Autowired
    private IMerchantInfoService merchantInfoService;


    @Override
    public BigDecimal payoutFailed(long betId) {
        GameBet gameBet = gameBetMapper.selectGameBetById(betId);
        if (gameBet == null) {
            throw new ServiceException("不存在对应的下注记录");
        }

//        if (!gameBet.getStatus().equals(GameEnum.BetStatusEnum.WT_FAILED.getStatus())) {
//            throw new ServiceException("只有派奖失败才能操作");
//        }

        if (gameBet.getPayoutAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("派奖金额小于等于0");
        }

        GameUserInfo gameUserInfo = gameUserInfoMapper.selectGameUserInfoById(gameBet.getUid());
        MessageUser messageUser = new MessageUser();
        messageUser.setUsername(gameUserInfo.getName());
        messageUser.setMerchantCode(gameUserInfo.getFromCode());
        messageUser.setOpenId(gameUserInfo.getOpenId());
        messageUser.setUid(gameUserInfo.getId().intValue());
        MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(gameUserInfo.getFromCode());
        messageUser.setSignKey(merchantInfo.getSignKey());
        //支付
        //派奖
        SeamlessResult payResult = paymentService.payout(gameBet, messageUser);
        if (payResult.isSuccess()) {
            GamePayout gamePayout = new GamePayout();
            gamePayout.setGameCode(gameBet.getGameCode());
            gamePayout.setBetId(gameBet.getId());
            gamePayout.setUid(gameUserInfo.getId());
            gamePayout.setAmount(gameBet.getPayoutAmount());
            gamePayout.setMultiplier(gameBet.getMultiplier());
            gamePayout.setOpenId(messageUser.getOpenId());
            gamePayout.setCreateTime(DateUtils.getNowDate());
            gamePayout.setUpdateTime(gamePayout.getCreateTime());
            gamePayoutMapper.insertGamePayout(gamePayout);
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBet.setStatus(GameEnum.BetStatusEnum.WT_DONE.getStatus());
            gameBetMapper.updateGameBet(gameBet);

            //如果遇到一直提现不成功，不能开新局 ，要么继续玩输，要么赢了也可能提现不成功？？？？
            if(gameBet.getGameCode().equals(Constants.MINES_GAME_CODE)){
                GameBet finalGameBet = gameBet;
                threadPoolTaskExecutor.execute(() -> redisTemplate.delete(RedisConstants.getKeyOngoingGame(gameUserInfo.getId(), finalGameBet.getGameCode())));
                gameRankService.addRankRecord(gameBet, gameBet.getGameCode());
            }
        } else {
            throw new ServiceException("派奖失败,请稍后重试");
        }
        return gameBet.getPayoutAmount();
    }

    @Override
    public BigDecimal refund(long betId) {

        GameBet gameBet = gameBetMapper.selectGameBetById(betId);
        if (gameBet == null) {
            throw new ServiceException("不存在对应的下注记录");
        }

//        if (!gameBet.getStatus().equals(GameEnum.BetStatusEnum.WT_FAILED.getStatus()) &&
//                !gameBet.getStatus().equals(GameEnum.BetStatusEnum.BET_DONE.getStatus())&&
//                !gameBet.getStatus().equals(GameEnum.BetStatusEnum.BET_FAILED.getStatus())) {//下注失败也允许，可能是超时引起的
//            throw new ServiceException("只允许派奖失败或者下注成功未完成游戏退回投注");
//        }

        if (gameBet.getBetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("下注金额小于等于0");
        }

        GameUserInfo gameUserInfo = gameUserInfoMapper.selectGameUserInfoById(gameBet.getUid());
        MessageUser messageUser = new MessageUser();
        messageUser.setUsername(gameUserInfo.getName());
        messageUser.setMerchantCode(gameUserInfo.getFromCode());
        messageUser.setOpenId(gameUserInfo.getOpenId());
        messageUser.setUid(gameUserInfo.getId().intValue());
        MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(gameUserInfo.getFromCode());
        messageUser.setSignKey(merchantInfo.getSignKey());
        //支付
        //退款
        SeamlessResult payResult = paymentService.refund(gameBet, messageUser, GameEnum.PaymentBusinessTypeEnum.REFUND);
        if (payResult.isSuccess()) {
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBet.setStatus(GameEnum.BetStatusEnum.REFUND.getStatus());
            gameBetMapper.updateGameBet(gameBet);
        } else {
            throw new ServiceException("退回投注失败,请稍后重试");
        }
        return gameBet.getBetAmount();
    }
}
