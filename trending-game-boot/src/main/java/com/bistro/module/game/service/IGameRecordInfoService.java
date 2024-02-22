package com.bistro.module.game.service;

import com.bistro.module.game.domain.GameRecordInfo;

import java.util.List;

/**
 * 游戏记录Service接口
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public interface IGameRecordInfoService 
{
    /**
     * 查询游戏记录
     * 
     * @param id 游戏记录主键
     * @return 游戏记录
     */
     GameRecordInfo selectGameRecordInfoById(Long id);

    /**
     * 查询游戏记录列表
     * 
     * @param gameRecordInfo 游戏记录
     * @return 游戏记录集合
     */
    List<GameRecordInfo> selectGameRecordInfoList(GameRecordInfo gameRecordInfo);

    /**
     * 新增游戏记录
     * 
     * @param gameRecordInfo 游戏记录
     * @return 结果
     */
    int insertGameRecordInfo(GameRecordInfo gameRecordInfo);

    /**
     * 修改游戏记录
     * 
     * @param gameRecordInfo 游戏记录
     * @return 结果
     */
    int updateGameRecordInfo(GameRecordInfo gameRecordInfo);

    /**
     * 批量删除游戏记录
     * 
     * @param ids 需要删除的游戏记录主键集合
     * @return 结果
     */
    int deleteGameRecordInfoByIds(Long[] ids);

    /**
     * 删除游戏记录信息
     * 
     * @param id 游戏记录主键
     * @return 结果
     */
    int deleteGameRecordInfoById(Long id);

    GameRecordInfo selectGameRecordInfoByBetId(Long betId);
}
