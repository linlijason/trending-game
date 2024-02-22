package com.bistro.module.game.mapper;

import com.bistro.module.game.domain.GamePeriod;

import java.util.List;

/**
 * 奖期管理Mapper接口
 * 
 * @author jason.lin
 * @date 2021-12-30
 */
public interface GamePeriodMapper 
{
    /**
     * 查询奖期管理
     * 
     * @param id 奖期管理主键
     * @return 奖期管理
     */
     GamePeriod selectGamePeriodById(Long id);

    /**
     * 查询奖期管理列表
     * 
     * @param gamePeriod 奖期管理
     * @return 奖期管理集合
     */
    List<GamePeriod> selectGamePeriodList(GamePeriod gamePeriod);

    /**
     * 新增奖期管理
     * 
     * @param gamePeriod 奖期管理
     * @return 结果
     */
    int insertGamePeriod(GamePeriod gamePeriod);

    /**
     * 修改奖期管理
     * 
     * @param gamePeriod 奖期管理
     * @return 结果
     */
    int updateGamePeriod(GamePeriod gamePeriod);

    /**
     * 删除奖期管理
     * 
     * @param id 奖期管理主键
     * @return 结果
     */
    int deleteGamePeriodById(Long id);

    /**
     * 批量删除奖期管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGamePeriodByIds(Long[] ids);


    int updateGamePeriodDrawInfo(GamePeriod gamePeriod);

}
