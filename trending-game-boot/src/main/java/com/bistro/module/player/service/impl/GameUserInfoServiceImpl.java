package com.bistro.module.player.service.impl;

import java.util.List;
import com.bistro.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bistro.module.player.mapper.GameUserInfoMapper;
import com.bistro.module.player.domain.GameUserInfo;
import com.bistro.module.player.service.IGameUserInfoService;

/**
 * 玩家信息Service业务层处理
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
@Service
public class GameUserInfoServiceImpl implements IGameUserInfoService 
{
    @Autowired
    private GameUserInfoMapper gameUserInfoMapper;

    /**
     * 查询玩家信息
     * 
     * @param id 玩家信息主键
     * @return 玩家信息
     */
    @Override
    public GameUserInfo selectGameUserInfoById(Long id)
    {
        return gameUserInfoMapper.selectGameUserInfoById(id);
    }

    /**
     * 查询玩家信息列表
     * 
     * @param gameUserInfo 玩家信息
     * @return 玩家信息
     */
    @Override
    public List<GameUserInfo> selectGameUserInfoList(GameUserInfo gameUserInfo)
    {
        return gameUserInfoMapper.selectGameUserInfoList(gameUserInfo);
    }

    /**
     * 新增玩家信息
     * 
     * @param gameUserInfo 玩家信息
     * @return 结果
     */
    @Override
    public int insertGameUserInfo(GameUserInfo gameUserInfo)
    {
        gameUserInfo.setCreateTime(DateUtils.getNowDate());
        return gameUserInfoMapper.insertGameUserInfo(gameUserInfo);
    }

    /**
     * 修改玩家信息
     * 
     * @param gameUserInfo 玩家信息
     * @return 结果
     */
    @Override
    public int updateGameUserInfo(GameUserInfo gameUserInfo)
    {
        gameUserInfo.setUpdateTime(DateUtils.getNowDate());
        return gameUserInfoMapper.updateGameUserInfo(gameUserInfo);
    }

    /**
     * 批量删除玩家信息
     * 
     * @param ids 需要删除的玩家信息主键
     * @return 结果
     */
    @Override
    public int deleteGameUserInfoByIds(Long[] ids)
    {
        return gameUserInfoMapper.deleteGameUserInfoByIds(ids);
    }

    /**
     * 删除玩家信息信息
     * 
     * @param id 玩家信息主键
     * @return 结果
     */
    @Override
    public int deleteGameUserInfoById(Long id)
    {
        return gameUserInfoMapper.deleteGameUserInfoById(id);
    }
}
