package com.bistro.module.game.service;

import com.bistro.module.api.vo.*;
import com.bistro.module.game.domain.GameBet;

import java.math.BigDecimal;
import java.util.List;

public interface IGameRankService {

    List<GamePlayInfo> selectBetHistoryByUid(GamePlayRequest request);

    List<GamePlayInfo> selectBetHistory(GamePlayRequest request);

    GameBetDetail getGameBetDetailByHashBetId(String hashBetId);

    void addRankRecord(GamePlayInfo gamePlayInfo, String gameCode);

    List<GamePlayInfo> selectRank(GamePlayRequest request);

    void cleanRankRecord(int size, String gameCode);

    void addRankRecord(GameBet gameBet, String gameCode);

    /**
     * 开奖前榜单
     *
     * @param coinCode
     * @param periodId
     */
    void addCoinRankBeforeDraw(Long betId, String coinCode, Long periodId, List<CoinBetRequest.BetContent> contents, BigDecimal betAmount, String user);

    /**
     * 开奖后更新榜单
     * @param coinCode
     * @param periodId
     * @param gameBetList
     */
    void updateCoinRankAfterDraw(String coinCode, Long periodId, List<GameBet> gameBetList);

    List<CoinRankInfo> selectCoinRank(CoinRankRequest request);

    void removeCoinRankBeforeDraw(Long betId, String coinCode, Long periodId, List<CoinBetRequest.BetContent> contents, BigDecimal betAmount, String user);


}
