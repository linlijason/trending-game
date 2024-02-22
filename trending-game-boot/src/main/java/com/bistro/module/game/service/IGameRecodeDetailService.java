package com.bistro.module.game.service;

import java.util.List;
import com.bistro.module.game.domain.GameRecodeDetail;

/**
 * 游戏记录详情Service接口
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public interface IGameRecodeDetailService 
{
    /**
     * 查询游戏记录详情
     * 
     * @param id 游戏记录详情主键
     * @return 游戏记录详情
     */
     GameRecodeDetail selectGameRecodeDetailById(Long id);

    /**
     * 查询游戏记录详情列表
     * 
     * @param gameRecodeDetail 游戏记录详情
     * @return 游戏记录详情集合
     */
    List<GameRecodeDetail> selectGameRecodeDetailList(GameRecodeDetail gameRecodeDetail);

    /**
     * 新增游戏记录详情
     * 
     * @param gameRecodeDetail 游戏记录详情
     * @return 结果
     */
    int insertGameRecodeDetail(GameRecodeDetail gameRecodeDetail);

    /**
     * 修改游戏记录详情
     * 
     * @param gameRecodeDetail 游戏记录详情
     * @return 结果
     */
    int updateGameRecodeDetail(GameRecodeDetail gameRecodeDetail);

    /**
     * 批量删除游戏记录详情
     * 
     * @param ids 需要删除的游戏记录详情主键集合
     * @return 结果
     */
    int deleteGameRecodeDetailByIds(Long[] ids);

    /**
     * 删除游戏记录详情信息
     * 
     * @param id 游戏记录详情主键
     * @return 结果
     */
    int deleteGameRecodeDetailById(Long id);
}
