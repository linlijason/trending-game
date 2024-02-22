package com.bistro.module.game.service;

import java.util.List;
import com.bistro.module.game.domain.GamePayout;

/**
 * 提现Service接口
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public interface IGamePayoutService 
{
    /**
     * 查询提现
     * 
     * @param id 提现主键
     * @return 提现
     */
     GamePayout selectGamePayoutById(Long id);

    /**
     * 查询提现列表
     * 
     * @param gamePayout 提现
     * @return 提现集合
     */
    List<GamePayout> selectGamePayoutList(GamePayout gamePayout);

    /**
     * 新增提现
     * 
     * @param gamePayout 提现
     * @return 结果
     */
    int insertGamePayout(GamePayout gamePayout);

    /**
     * 修改提现
     * 
     * @param gamePayout 提现
     * @return 结果
     */
    int updateGamePayout(GamePayout gamePayout);

    /**
     * 批量删除提现
     * 
     * @param ids 需要删除的提现主键集合
     * @return 结果
     */
    int deleteGamePayoutByIds(Long[] ids);

    /**
     * 删除提现信息
     * 
     * @param id 提现主键
     * @return 结果
     */
    int deleteGamePayoutById(Long id);
}
