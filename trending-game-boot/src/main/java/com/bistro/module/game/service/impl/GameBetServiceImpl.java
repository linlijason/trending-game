package com.bistro.module.game.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bistro.aop.RedissonLock;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.common.utils.DateUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.message.model.BetMessage;
import com.bistro.message.model.MessageUser;
import com.bistro.module.game.domain.GameBaseInfo;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.GameEnum;
import com.bistro.module.game.domain.GameRecordInfo;
import com.bistro.module.game.mapper.GameBaseInfoMapper;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.mapper.GameRecordInfoMapper;
import com.bistro.module.game.service.IGameBetService;
import com.bistro.module.payment.service.IPaymentService;
import com.bistro.module.seamless.client.SeamlessResult;
import com.bistro.utils.BetidHashUtils;
import com.bistro.utils.MultiplierUtils;
import com.bistro.utils.RandomNumbersUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 下注信息Service业务层处理
 *
 * @author gavin
 * @date 2021-11-17
 */
@Service
public class GameBetServiceImpl implements IGameBetService {

    private static final Logger log = LoggerFactory.getLogger(GameBetServiceImpl.class);

    @Autowired
    private GameBetMapper gameBetMapper;

    @Autowired
    private GameRecordInfoMapper gameRecordInfoMapper;

    @Autowired
    private GameBaseInfoMapper baseInfoMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 查询下注信息
     *
     * @param id 下注信息主键
     * @return 下注信息
     */
    @Override
    public GameBet selectGameBetById(Long id) {
        return gameBetMapper.selectGameBetById(id);
    }

    /**
     * 查询下注信息列表
     *
     * @param gameBet 下注信息
     * @return 下注信息
     */
    @Override
    public List<GameBet> selectGameBetList(GameBet gameBet) {
        return gameBetMapper.selectGameBetList(gameBet);
    }

    /**
     * 新增下注信息
     *
     * @param gameBet 下注信息
     * @return 结果
     */
    @Override
    public int insertGameBet(GameBet gameBet) {
        return gameBetMapper.insertGameBet(gameBet);
    }

    /**
     * 修改下注信息
     *
     * @param gameBet 下注信息
     * @return 结果
     */
    @Override
    public int updateGameBet(GameBet gameBet) {
        gameBet.setUpdateTime(DateUtils.getNowDate());
        return gameBetMapper.updateGameBet(gameBet);
    }

    /**
     * 批量删除下注信息
     *
     * @param ids 需要删除的下注信息主键
     * @return 结果
     */
    @Override
    public int deleteGameBetByIds(Long[] ids) {
        return gameBetMapper.deleteGameBetByIds(ids);
    }

    /**
     * 删除下注信息信息
     *
     * @param id 下注信息主键
     * @return 结果
     */
    @Override
    public int deleteGameBetById(Long id) {
        return gameBetMapper.deleteGameBetById(id);
    }

    @Override
    @RedissonLock(keyParam = "#fromUser.uid + '_60'")
    @Transactional(rollbackFor = Exception.class)
    public BetMessage.Response playerBet(MessageUser fromUser, BetMessage.Request request) {
        Long uid = Long.valueOf(fromUser.getUid());
        String openId = fromUser.getOpenId();
        BigDecimal betAmount = new BigDecimal(request.getBetAmount());
        Date now = DateUtils.getNowDate();

        int minesCount = request.getMinesCount();
        if (minesCount < Constants.SELECT_MINES_BLOCKS_MIN || minesCount >= Constants.MINES_BLOCKS) {
            throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
        }
        GameBaseInfo gameBaseInfo = baseInfoMapper.selectGameBaseInfoByCode(request.getGameCode());

        if (gameBaseInfo == null) {
            throw new ApiException(ApiExceptionMsgEnum.COM_PARAM_ERROR.getCode(), "param error");
        }

        if (gameBaseInfo.getIsOpen() == 0) {
            throw new ApiException(ApiExceptionMsgEnum.COM_NOT_OPEN_ERROR.getCode(), "game is temporarily closed");
        }

        if (betAmount.compareTo(gameBaseInfo.getMinBetAmount()) < 0) {
            throw new ApiException(ApiExceptionMsgEnum.BET_AMOUNT_MIN_LIMIT.getCode(), "min amount limit");
        }

        if (betAmount.compareTo(gameBaseInfo.getMaxBetAmount()) > 0) {
            throw new ApiException(ApiExceptionMsgEnum.BET_AMOUNT_MAX_LIMIT.getCode(), "max amount limit");
        }


        //插入bet数据
        GameBet gameBet = new GameBet();
        gameBet.setGameCode(request.getGameCode());
        gameBet.setBetAmount(betAmount);
        gameBet.setStatus(GameEnum.BetStatusEnum.NEW.getStatus());
        gameBet.setCreateTime(now);
        gameBet.setUpdateTime(now);
        gameBet.setPayoutAmount(betAmount);//初始为投注金额
        gameBet.setUid(uid);
        gameBet.setCurrency(request.getCurrency());
        gameBet.setOpenId(openId);
        gameBet.setUserName(fromUser.getUsername());

        gameBetMapper.insertGameBet(gameBet);
        long betId = gameBet.getId();
        //扣款
        SeamlessResult payResult = paymentService.bet(gameBet, fromUser);
        //扣款成功
        if (payResult.isSuccess()) {
            gameBet.setStatus(GameEnum.BetStatusEnum.BET_DONE.getStatus());
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBetMapper.updateGameBet(gameBet);

            //生成游戏记录
            //随机雷
            //字段少 先这样吧
            JSONObject content = new JSONObject();
            content.put(Constants.MINES_BET_MULTIPLIER, MultiplierUtils.getMultiplierMap(request.getMinesCount(), gameBaseInfo.getMimesConfigs()));
            content.put(Constants.MINES_BET_CURRENCY, request.getCurrency());
            content.put(Constants.MINES_MAX_PAYOUT_AMOUNT, gameBaseInfo.getMaxPayoutAmount().stripTrailingZeros().toPlainString());
            content.put(Constants.MINES_BET_AMOUNT, request.getBetAmount());//也存入content 不从gamebet里再取一次了
            content.put(Constants.MINES_INDEX_ARRAY,
                    RandomNumbersUtils.randomArray(0, Constants.MINES_BLOCKS - 1, minesCount));
            content.put(Constants.MINES_COMMISSION_RATE, gameBaseInfo.getCommissionRate().stripTrailingZeros().toPlainString());
            GameRecordInfo gameRecordInfo = new GameRecordInfo();
            gameRecordInfo.setGameCode(request.getGameCode());
            gameRecordInfo.setBetId(betId);
            gameRecordInfo.setGameContent(content.toJSONString());
            gameRecordInfo.setCreateTime(now);
            gameRecordInfo.setUpdateTime(now);
            gameRecordInfo.setUid(uid);
            gameRecordInfo.setOpenId(openId);
            gameRecordInfoMapper.insertGameRecordInfo(gameRecordInfo);
            BetMessage.Response response = new BetMessage.Response();
            response.setBetId(BetidHashUtils.hashId(betId));
            response.setIsPayed(1);
            response.setBalance(payResult.getBalance().stripTrailingZeros().toPlainString());
            response.setCurrency(payResult.getCurrency());

            threadPoolTaskExecutor.execute(() -> redisTemplate.opsForValue().set(RedisConstants.getKeyOngoingGame(uid, request.getGameCode()), String.valueOf(betId)));

            return response;

        } else {
            //扣款失败
            gameBet.setStatus(GameEnum.BetStatusEnum.BET_FAILED.getStatus());
            gameBet.setUpdateTime(DateUtils.getNowDate());
            gameBetMapper.updateGameBet(gameBet);
            BetMessage.Response response = new BetMessage.Response();
            response.setBetId(BetidHashUtils.hashId(betId));
            response.setIsPayed(0);
            response.setErrorMessage(payResult.getOriginalErrMsg());
            return response;
        }
    }
}
