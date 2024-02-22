package com.bistro.module.game.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.common.utils.DateUtils;
import com.bistro.constants.Constants;
import com.bistro.module.api.vo.CoinGameInfo;
import com.bistro.module.api.vo.CurrencyVo;
import com.bistro.module.api.vo.GameInfo;
import com.bistro.module.game.domain.GameBaseInfo;
import com.bistro.module.game.domain.ext.MineAndHitConfigDto;
import com.bistro.module.game.mapper.CurrencyInfoMapper;
import com.bistro.module.game.mapper.GameBaseInfoMapper;
import com.bistro.module.game.service.IGameBaseInfoService;
import com.bistro.web.core.config.CoinConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 游戏基本信息Service业务层处理
 *
 * @author gavin
 * @date 2021-11-17
 */
@Service
public class GameBaseInfoServiceImpl implements IGameBaseInfoService {
    @Autowired
    private GameBaseInfoMapper gameBaseInfoMapper;

    @Autowired
    private CurrencyInfoMapper currencyInfoMapper;

    @Autowired
    private CoinConfig coinConfig;

    /**
     * 查询游戏基本信息
     *
     * @param id 游戏基本信息主键
     * @return 游戏基本信息
     */
    @Override
    public GameBaseInfo selectGameBaseInfoById(Integer id) {
        var gameInfo = gameBaseInfoMapper.selectGameBaseInfoById(id);
        return gameInfo;
    }

    /**
     * 查询游戏基本信息列表
     *
     * @param gameBaseInfo 游戏基本信息
     * @return 游戏基本信息
     */
    @Override
    public List<GameBaseInfo> selectGameBaseInfoList(GameBaseInfo gameBaseInfo) {
        return gameBaseInfoMapper.selectGameBaseInfoList(gameBaseInfo);
    }

    /**
     * 新增游戏基本信息
     *
     * @param gameBaseInfo 游戏基本信息
     * @return 结果
     */
    @Override
    public int insertGameBaseInfo(GameBaseInfo gameBaseInfo) {
        gameBaseInfo.setCreateTime(DateUtils.getNowDate());
        return gameBaseInfoMapper.insertGameBaseInfo(gameBaseInfo);
    }

    /**
     * 修改游戏基本信息
     *
     * @param gameBaseInfo 游戏基本信息
     * @return 结果
     */
    @Override
    public int updateGameBaseInfo(GameBaseInfo gameBaseInfo) {
        gameBaseInfo.setUpdateTime(DateUtils.getNowDate());
        if (GameBaseInfo.MIMES_CODE.equals(gameBaseInfo.getCode())
                && StringUtils.isNotEmpty(gameBaseInfo.getExtConfig())) {
            var pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
            var configArr = JSON.parseArray(gameBaseInfo.getExtConfig()
                            , MineAndHitConfigDto.class)
                    .stream().filter(mm ->
                            StringUtils.isNotEmpty(mm.getMultiplier2()) &&
                                    pattern.matcher(mm.getMultiplier2()).matches()
                    ).collect(Collectors.groupingBy(MineAndHitConfigDto::getMinesCount));
            if (configArr.size() > 0) {
                Map<String, Map<String, BigDecimal>> map = new HashMap<>();
                configArr.entrySet().forEach(kv -> {
                    var hitMap = new HashMap<String, BigDecimal>();
                    for (MineAndHitConfigDto dto : kv.getValue()) {
                        hitMap.put(dto.getHits(), new BigDecimal(dto.getMultiplier2()));
                    }
                    map.put(kv.getKey(), hitMap);
                });
                gameBaseInfo.setExtConfig(JSON.toJSONString(map));
            } else {
                gameBaseInfo.setExtConfig("{}");
            }

        }
        return gameBaseInfoMapper.updateGameBaseInfo(gameBaseInfo);
    }

    /**
     * 批量删除游戏基本信息
     *
     * @param ids 需要删除的游戏基本信息主键
     * @return 结果
     */
    @Override
    public int deleteGameBaseInfoByIds(Integer[] ids) {
        return gameBaseInfoMapper.deleteGameBaseInfoByIds(ids);
    }

    /**
     * 删除游戏基本信息信息
     *
     * @param id 游戏基本信息主键
     * @return 结果
     */
    @Override
    public int deleteGameBaseInfoById(Integer id) {
        return gameBaseInfoMapper.deleteGameBaseInfoById(id);
    }

    @Override
    public GameInfo getGameInfo(String code) {

        GameBaseInfo gameBaseInfo = gameBaseInfoMapper.selectGameBaseInfoByCode(code);

        if (gameBaseInfo == null) {
            throw new ApiException(ApiExceptionMsgEnum.GAME_NOT_FOUND.getCode(), "game not found");
        }
        BigDecimal commissionRate = gameBaseInfo.getCommissionRate();
        GameInfo.Limit limit = new GameInfo.Limit();
        limit.setMaxBetAmount(gameBaseInfo.getMaxBetAmount());
        limit.setMaxPayoutAmount(gameBaseInfo.getMaxPayoutAmount());
        limit.setMinBetAmount(gameBaseInfo.getMinBetAmount());

        //构造limits list, 现阶段只有BRL, 为了后期前端不需要改动，先返回list(没做每种currency不一样的limit todo)
        List<CurrencyVo> currencyVos = currencyInfoMapper.selectCurrencyList();
        List<GameInfo.Limit> limits = new ArrayList<>();
        for (CurrencyVo currencyItem : currencyVos) {
            GameInfo.Limit limitItem = new GameInfo.Limit();
            limitItem.setCurrencyCode(currencyItem.getCurrencyCode());
            limitItem.setMinBetAmount(gameBaseInfo.getMinBetAmount());
            limitItem.setMaxBetAmount(gameBaseInfo.getMaxBetAmount());
            limitItem.setMaxPayoutAmount(gameBaseInfo.getMaxPayoutAmount());
            limits.add(limitItem);
        }
        GameInfo gameInfo = new GameInfo();
        gameInfo.setLimit(limit);
        gameInfo.setLimits(limits);
        gameInfo.setMultiplierConfig(JSONObject.parseArray(JSONObject.toJSONString(gameBaseInfo.getMimesConfigs()), GameInfo.MultiplierConfigItem.class));
        gameInfo.setCurrency(currencyVos);
        gameInfo.setGameCode(code);
        return gameInfo;
    }

    @Override
    public CoinGameInfo getCoinGameInfo() {
        GameBaseInfo gameBaseInfo = gameBaseInfoMapper.selectGameBaseInfoByCode(Constants.COIN_GAME_CODE);

        if (gameBaseInfo == null) {
            throw new ApiException(ApiExceptionMsgEnum.GAME_NOT_FOUND.getCode(), "game not found");
        }
        BigDecimal commissionRate = gameBaseInfo.getCommissionRate();
        GameInfo.Limit limit = new GameInfo.Limit();
        limit.setMaxBetAmount(gameBaseInfo.getMaxBetAmount());
        limit.setMaxPayoutAmount(gameBaseInfo.getMaxPayoutAmount());
        limit.setMinBetAmount(gameBaseInfo.getMinBetAmount());

        //构造limits list, 现阶段只有BRL, 为了后期前端不需要改动，先返回list(没做每种currency不一样的limit todo)
        List<CurrencyVo> currencyVos = currencyInfoMapper.selectCurrencyList();
        List<GameInfo.Limit> limits = new ArrayList<>();
        for (CurrencyVo currencyItem : currencyVos) {
            GameInfo.Limit limitItem = new GameInfo.Limit();
            limitItem.setCurrencyCode(currencyItem.getCurrencyCode());
            limitItem.setMinBetAmount(gameBaseInfo.getMinBetAmount());
            limitItem.setMaxBetAmount(gameBaseInfo.getMaxBetAmount());
            limitItem.setMaxPayoutAmount(gameBaseInfo.getMaxPayoutAmount());
            limits.add(limitItem);
        }
        CoinGameInfo gameInfo = new CoinGameInfo();
        gameInfo.setLimit(limit);
        gameInfo.setLimits(limits);
        gameInfo.setCurrency(currencyVos);
        gameInfo.setDefaultBetAmount(gameBaseInfo.getMinBetAmount().stripTrailingZeros().toPlainString());//配置？or 数据库 todo
        gameInfo.setOptions(gameBaseInfo.getCoinGuessOptions());
        gameInfo.setCoins(coinConfig.getSupports());
        gameInfo.setBetDuration(coinConfig.getBetDuration());
        gameInfo.setDrawDuration(coinConfig.getDrawDuration());
        return gameInfo;
    }
}
