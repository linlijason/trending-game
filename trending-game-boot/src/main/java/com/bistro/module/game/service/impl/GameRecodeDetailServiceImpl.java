package com.bistro.module.game.service.impl;

import java.util.List;
import com.bistro.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bistro.module.game.mapper.GameRecodeDetailMapper;
import com.bistro.module.game.domain.GameRecodeDetail;
import com.bistro.module.game.service.IGameRecodeDetailService;

/**
 * 游戏记录详情Service业务层处理
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
@Service
public class GameRecodeDetailServiceImpl implements IGameRecodeDetailService 
{
    @Autowired
    private GameRecodeDetailMapper gameRecodeDetailMapper;

    /**
     * 查询游戏记录详情
     * 
     * @param id 游戏记录详情主键
     * @return 游戏记录详情
     */
    @Override
    public GameRecodeDetail selectGameRecodeDetailById(Long id)
    {
        return gameRecodeDetailMapper.selectGameRecodeDetailById(id);
    }

    /**
     * 查询游戏记录详情列表
     * 
     * @param gameRecodeDetail 游戏记录详情
     * @return 游戏记录详情
     */
    @Override
    public List<GameRecodeDetail> selectGameRecodeDetailList(GameRecodeDetail gameRecodeDetail)
    {
        return gameRecodeDetailMapper.selectGameRecodeDetailList(gameRecodeDetail);
    }

    /**
     * 新增游戏记录详情
     * 
     * @param gameRecodeDetail 游戏记录详情
     * @return 结果
     */
    @Override
    public int insertGameRecodeDetail(GameRecodeDetail gameRecodeDetail)
    {
        gameRecodeDetail.setCreateTime(DateUtils.getNowDate());
        return gameRecodeDetailMapper.insertGameRecodeDetail(gameRecodeDetail);
    }

    /**
     * 修改游戏记录详情
     * 
     * @param gameRecodeDetail 游戏记录详情
     * @return 结果
     */
    @Override
    public int updateGameRecodeDetail(GameRecodeDetail gameRecodeDetail)
    {
        return gameRecodeDetailMapper.updateGameRecodeDetail(gameRecodeDetail);
    }

    /**
     * 批量删除游戏记录详情
     * 
     * @param ids 需要删除的游戏记录详情主键
     * @return 结果
     */
    @Override
    public int deleteGameRecodeDetailByIds(Long[] ids)
    {
        return gameRecodeDetailMapper.deleteGameRecodeDetailByIds(ids);
    }

    /**
     * 删除游戏记录详情信息
     * 
     * @param id 游戏记录详情主键
     * @return 结果
     */
    @Override
    public int deleteGameRecodeDetailById(Long id)
    {
        return gameRecodeDetailMapper.deleteGameRecodeDetailById(id);
    }
}
