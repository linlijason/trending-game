package com.bistro.module.game.mapper;

import com.bistro.module.game.domain.GameBaseInfo;

import java.util.List;

/**
 * 游戏基本信息Mapper接口
 * 
 * @author gavin
 * @date 2021-11-17
 */
public interface GameBaseInfoMapper 
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
     * 删除游戏基本信息
     * 
     * @param id 游戏基本信息主键
     * @return 结果
     */
    int deleteGameBaseInfoById(Integer id);

    /**
     * 批量删除游戏基本信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGameBaseInfoByIds(Integer[] ids);

    /**
     * 查询游戏基本信息
     *
     * @param gameCode
     * @return 游戏基本信息
     */
    GameBaseInfo selectGameBaseInfoByCode(String gameCode);

}
