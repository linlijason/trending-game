package com.bistro.module.game.service.impl;

import java.util.List;

import com.bistro.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bistro.module.game.mapper.GameRecordInfoMapper;
import com.bistro.module.game.domain.GameRecordInfo;
import com.bistro.module.game.service.IGameRecordInfoService;

/**
 * 游戏记录Service业务层处理
 *
 * @author jason.lin
 * @date 2021-11-17
 */
@Service
public class GameRecordInfoServiceImpl implements IGameRecordInfoService {
    @Autowired
    private GameRecordInfoMapper gameRecordInfoMapper;

    /**
     * 查询游戏记录
     *
     * @param id 游戏记录主键
     * @return 游戏记录
     */
    @Override
    public GameRecordInfo selectGameRecordInfoById(Long id) {
        return gameRecordInfoMapper.selectGameRecordInfoById(id);
    }

    /**
     * 查询游戏记录列表
     *
     * @param gameRecordInfo 游戏记录
     * @return 游戏记录
     */
    @Override
    public List<GameRecordInfo> selectGameRecordInfoList(GameRecordInfo gameRecordInfo) {
        return gameRecordInfoMapper.selectGameRecordInfoList(gameRecordInfo);
    }

    /**
     * 新增游戏记录
     *
     * @param gameRecordInfo 游戏记录
     * @return 结果
     */
    @Override
    public int insertGameRecordInfo(GameRecordInfo gameRecordInfo) {
        gameRecordInfo.setCreateTime(DateUtils.getNowDate());
        return gameRecordInfoMapper.insertGameRecordInfo(gameRecordInfo);
    }

    /**
     * 修改游戏记录
     *
     * @param gameRecordInfo 游戏记录
     * @return 结果
     */
    @Override
    public int updateGameRecordInfo(GameRecordInfo gameRecordInfo) {
        gameRecordInfo.setUpdateTime(DateUtils.getNowDate());
        return gameRecordInfoMapper.updateGameRecordInfo(gameRecordInfo);
    }

    /**
     * 批量删除游戏记录
     *
     * @param ids 需要删除的游戏记录主键
     * @return 结果
     */
    @Override
    public int deleteGameRecordInfoByIds(Long[] ids) {
        return gameRecordInfoMapper.deleteGameRecordInfoByIds(ids);
    }

    /**
     * 删除游戏记录信息
     *
     * @param id 游戏记录主键
     * @return 结果
     */
    @Override
    public int deleteGameRecordInfoById(Long id) {
        return gameRecordInfoMapper.deleteGameRecordInfoById(id);
    }

    @Override
    public GameRecordInfo selectGameRecordInfoByBetId(Long betId) {
        return gameRecordInfoMapper.selectGameRecordInfoByBetId(betId);
    }
}
