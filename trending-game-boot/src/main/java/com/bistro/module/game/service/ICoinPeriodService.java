package com.bistro.module.game.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface ICoinPeriodService {


    void createCoinPeriod(String coin, Long time);

    /**
     * 当前奖期
     *
     * @param coin
     * @return
     */
    Map getCoinPeriod(String coin);


    /**
     * 最近X期 已开奖 （redis）
     *
     * @param coin
     * @return
     */
    List<JSONObject> getXPeriod(String coin, Integer length);

    /**
     * 开奖后将信息放在redis
     *
     * @param coin
     * @param coinPeriodId
     * @param coinPeriodMap
     */
    void addPeriodToSet(String coin, Long coinPeriodId, Map coinPeriodMap);


    /**
     * 从redis已开奖奖期
     *
     * @param coin
     * @param coinPeriodId
     * @return
     */
    JSONObject getPeriod(String coin, Long coinPeriodId);

    /**
     * 查询奖期，如果传了betid则把该次投注结果也返回 （redis）
     *
     * @param coin
     * @param coinPeriodId
     * @param betId
     * @return
     */
    JSONObject getPeriod(String coin, Long coinPeriodId, Long betId, Long uid);


    void updateCurrentPeriod(String coin, Map periodMap);

    void cleanHistoryDateRedis(String coin, int retainSize);


}
