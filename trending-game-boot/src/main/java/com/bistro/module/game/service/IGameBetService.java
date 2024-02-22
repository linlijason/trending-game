package com.bistro.module.game.service;

import com.bistro.message.model.BetMessage;
import com.bistro.message.model.MessageUser;
import com.bistro.module.game.domain.GameBet;

import java.util.List;

/**
 * 下注信息Service接口
 * 
 * @author gavin
 * @date 2021-11-17
 */
public interface IGameBetService 
{
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
     * 批量删除下注信息
     * 
     * @param ids 需要删除的下注信息主键集合
     * @return 结果
     */
    int deleteGameBetByIds(Long[] ids);

    /**
     * 删除下注信息信息
     * 
     * @param id 下注信息主键
     * @return 结果
     */
    int deleteGameBetById(Long id);

    BetMessage.Response playerBet(MessageUser from, BetMessage.Request request);
}
