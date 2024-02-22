package com.bistro.module.game.mapper;

import com.bistro.module.api.vo.GamePlayInfo;
import com.bistro.module.api.vo.GamePlayRequest;
import com.bistro.module.game.domain.*;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;

/**
 * 下注信息Mapper接口
 *
 * @author gavin
 * @date 2021-11-17
 */
public interface GameBetMapper {
    /**
     * 查询下注信息
     *
     * @param id 下注信息主键
     * @return 下注信息
     */
    GameBet selectGameBetById(Long id);

    /**
     * 查询下注信息列表
     *
     * @param gameBet 下注信息
     * @return 下注信息集合
     */
    List<GameBet> selectGameBetList(GameBet gameBet);

    /**
     * 新增下注信息
     *
     * @param gameBet 下注信息
     * @return 结果
     */
    int insertGameBet(GameBet gameBet);

    /**
     * 修改下注信息
     *
     * @param gameBet 下注信息
     * @return 结果
     */
    int updateGameBet(GameBet gameBet);

    /**
     * 删除下注信息
     *
     * @param id 下注信息主键
     * @return 结果
     */
    int deleteGameBetById(Long id);

    /**
     * 批量删除下注信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGameBetByIds(Long[] ids);

    List<GamePlayInfo> selectBetHistoryByUid(GamePlayRequest request);

    List<GamePlayInfo> selectBetHistory(GamePlayRequest request);

    List<BetGameUserVo> selectBetGameUser(BetGameUserQuery query);

    List<BetPeriodStatisticsVo> selectBetPeriodStatistics(BetPeriodStatisticsQuery query);

    List<BetGameRecordVo> selectBetRecord(BetGameRecordQuery query);

    int updateBatch(List<GameBet> list);

    int updateStatusBatch(List<GameBet> list);

    Cursor<BetGameUserVo> selectBetForExport(BetGameUserQuery query);

    BetPeriodStatisticsVo selectBetSummaryStatistics(BetPeriodStatisticsQuery query);

    List<BetPeriodStatisticsVo> selectBetSummaryStatisticsPerUser(BetPeriodStatisticsQuery query);

}
