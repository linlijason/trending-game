package com.bistro.module.game.mapper;

import com.bistro.module.game.domain.GameRecodeDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游戏记录详情Mapper接口
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public interface GameRecodeDetailMapper 
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
     * 删除游戏记录详情
     * 
     * @param id 游戏记录详情主键
     * @return 结果
     */
    int deleteGameRecodeDetailById(Long id);

    /**
     * 批量删除游戏记录详情
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGameRecodeDetailByIds(Long[] ids);

    int countGameRecodeDetailByBetId(Long betId);

    /**
     * 新增游戏记录详情
     *
     * @param gameRecodeDetails 游戏记录详情
     * @return 结果
     */
    int batchInsertGameRecodeDetail(@Param("gameRecodeDetails")List<GameRecodeDetail> gameRecodeDetails);

    //获取扫雷记录
    List<GameRecodeDetail> selectIndexShotList(Long betId);
}
