package com.bistro.module.player.service;

import java.util.List;
import com.bistro.module.player.domain.GameUserInfo;

/**
 * 玩家信息Service接口
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public interface IGameUserInfoService 
{
    /**
     * 查询玩家信息
     * 
     * @param id 玩家信息主键
     * @return 玩家信息
     */
     GameUserInfo selectGameUserInfoById(Long id);

    /**
     * 查询玩家信息列表
     * 
     * @param gameUserInfo 玩家信息
     * @return 玩家信息集合
     */
    List<GameUserInfo> selectGameUserInfoList(GameUserInfo gameUserInfo);

    /**
     * 新增玩家信息
     * 
     * @param gameUserInfo 玩家信息
     * @return 结果
     */
    int insertGameUserInfo(GameUserInfo gameUserInfo);

    /**
     * 修改玩家信息
     * 
     * @param gameUserInfo 玩家信息
     * @return 结果
     */
    int updateGameUserInfo(GameUserInfo gameUserInfo);

    /**
     * 批量删除玩家信息
     * 
     * @param ids 需要删除的玩家信息主键集合
     * @return 结果
     */
    int deleteGameUserInfoByIds(Long[] ids);

    /**
     * 删除玩家信息信息
     * 
     * @param id 玩家信息主键
     * @return 结果
     */
    int deleteGameUserInfoById(Long id);
}
