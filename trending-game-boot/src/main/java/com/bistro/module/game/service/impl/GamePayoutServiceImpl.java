package com.bistro.module.game.service.impl;

import java.util.List;
import com.bistro.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bistro.module.game.mapper.GamePayoutMapper;
import com.bistro.module.game.domain.GamePayout;
import com.bistro.module.game.service.IGamePayoutService;

/**
 * 提现Service业务层处理
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
@Service
public class GamePayoutServiceImpl implements IGamePayoutService 
{
    @Autowired
    private GamePayoutMapper gamePayoutMapper;

    /**
     * 查询提现
     * 
     * @param id 提现主键
     * @return 提现
     */
    @Override
    public GamePayout selectGamePayoutById(Long id)
    {
        return gamePayoutMapper.selectGamePayoutById(id);
    }

    /**
     * 查询提现列表
     * 
     * @param gamePayout 提现
     * @return 提现
     */
    @Override
    public List<GamePayout> selectGamePayoutList(GamePayout gamePayout)
    {
        return gamePayoutMapper.selectGamePayoutList(gamePayout);
    }

    /**
     * 新增提现
     * 
     * @param gamePayout 提现
     * @return 结果
     */
    @Override
    public int insertGamePayout(GamePayout gamePayout)
    {
        gamePayout.setCreateTime(DateUtils.getNowDate());
        return gamePayoutMapper.insertGamePayout(gamePayout);
    }

    /**
     * 修改提现
     * 
     * @param gamePayout 提现
     * @return 结果
     */
    @Override
    public int updateGamePayout(GamePayout gamePayout)
    {
        gamePayout.setUpdateTime(DateUtils.getNowDate());
        return gamePayoutMapper.updateGamePayout(gamePayout);
    }

    /**
     * 批量删除提现
     * 
     * @param ids 需要删除的提现主键
     * @return 结果
     */
    @Override
    public int deleteGamePayoutByIds(Long[] ids)
    {
        return gamePayoutMapper.deleteGamePayoutByIds(ids);
    }

    /**
     * 删除提现信息
     * 
     * @param id 提现主键
     * @return 结果
     */
    @Override
    public int deleteGamePayoutById(Long id)
    {
        return gamePayoutMapper.deleteGamePayoutById(id);
    }
}
