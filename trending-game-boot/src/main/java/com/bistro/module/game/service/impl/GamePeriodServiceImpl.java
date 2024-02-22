package com.bistro.module.game.service.impl;

import com.bistro.module.game.domain.GamePeriod;
import com.bistro.module.game.mapper.GamePeriodMapper;
import com.bistro.module.game.service.IGamePeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 奖期管理Service业务层处理
 * 
 * @author jason.lin
 * @date 2021-12-30
 */
@Service
public class GamePeriodServiceImpl implements IGamePeriodService 
{
    @Autowired
    private GamePeriodMapper gamePeriodMapper;

    /**
     * 查询奖期管理
     * 
     * @param id 奖期管理主键
     * @return 奖期管理
     */
    @Override
    public GamePeriod selectGamePeriodById(Long id)
    {
        return gamePeriodMapper.selectGamePeriodById(id);
    }

    /**
     * 查询奖期管理列表
     * 
     * @param gamePeriod 奖期管理
     * @return 奖期管理
     */
    @Override
    public List<GamePeriod> selectGamePeriodList(GamePeriod gamePeriod)
    {
        return gamePeriodMapper.selectGamePeriodList(gamePeriod);
    }

    /**
     * 新增奖期管理
     * 
     * @param gamePeriod 奖期管理
     * @return 结果
     */
    @Override
    public int insertGamePeriod(GamePeriod gamePeriod)
    {
        return gamePeriodMapper.insertGamePeriod(gamePeriod);
    }

    /**
     * 修改奖期管理
     * 
     * @param gamePeriod 奖期管理
     * @return 结果
     */
    @Override
    public int updateGamePeriod(GamePeriod gamePeriod)
    {
        return gamePeriodMapper.updateGamePeriod(gamePeriod);
    }

    /**
     * 批量删除奖期管理
     * 
     * @param ids 需要删除的奖期管理主键
     * @return 结果
     */
    @Override
    public int deleteGamePeriodByIds(Long[] ids)
    {
        return gamePeriodMapper.deleteGamePeriodByIds(ids);
    }

    /**
     * 删除奖期管理信息
     * 
     * @param id 奖期管理主键
     * @return 结果
     */
    @Override
    public int deleteGamePeriodById(Long id)
    {
        return gamePeriodMapper.deleteGamePeriodById(id);
    }
}
