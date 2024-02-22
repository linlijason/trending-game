package com.bistro.module.game.mapper;

import com.bistro.module.game.domain.GameBetRecordInfo;
import com.bistro.module.game.domain.GameBetRecordQuery;
import com.bistro.module.game.domain.GameRecordInfo;

import java.util.List;

/**
 * 游戏记录Mapper接口
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public interface GameRecordInfoMapper 
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
     * 删除游戏记录
     * 
     * @param id 游戏记录主键
     * @return 结果
     */
    int deleteGameRecordInfoById(Long id);

    /**
     * 批量删除游戏记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGameRecordInfoByIds(Long[] ids);

    /**
     * 查询游戏记录
     *
     * @param betId
     * @return 游戏记录
     */
    GameRecordInfo selectGameRecordInfoByBetId(Long betId);

    int batchUpdateContent(List<GameRecordInfo> list);

    List<GameBetRecordInfo> selectGameRecordByUidAndCode(GameBetRecordQuery query);
}
