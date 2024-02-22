package com.bistro.module.game.service;

import com.bistro.module.api.vo.CoinGameInfo;
import com.bistro.module.api.vo.GameInfo;
import com.bistro.module.game.domain.GameBaseInfo;

import java.util.List;

/**
 * 游戏基本信息Service接口
 * 
 * @author gavin
 * @date 2021-11-17
 */
public interface IGameBaseInfoService 
{
    /**
     * 查询游戏基本信息
     * 
     * @param id 游戏基本信息主键
     * @return 游戏基本信息
     */
    GameBaseInfo selectGameBaseInfoById(Integer id);

    /**
     * 查询游戏基本信息列表
     * 
     * @param gameBaseInfo 游戏基本信息
     * @return 游戏基本信息集合
     */
    List<GameBaseInfo> selectGameBaseInfoList(GameBaseInfo gameBaseInfo);

    /**
     * 新增游戏基本信息
     * 
     * @param gameBaseInfo 游戏基本信息
     * @return 结果
     */
    int insertGameBaseInfo(GameBaseInfo gameBaseInfo);

    /**
     * 修改游戏基本信息
     * 
     * @param gameBaseInfo 游戏基本信息
     * @return 结果
     */
    int updateGameBaseInfo(GameBaseInfo gameBaseInfo);

    /**
     * 批量删除游戏基本信息
     * 
     * @param ids 需要删除的游戏基本信息主键集合
     * @return 结果
     */
    int deleteGameBaseInfoByIds(Integer[] ids);

    /**
     * 删除游戏基本信息信息
     * 
     * @param id 游戏基本信息主键
     * @return 结果
     */
    int deleteGameBaseInfoById(Integer id);

    GameInfo getGameInfo(String code);

    CoinGameInfo getCoinGameInfo();
}
