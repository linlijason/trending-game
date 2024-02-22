package com.bistro.module.binance.service;

import com.alibaba.fastjson.JSONObject;
import com.bistro.module.binance.domain.TickerBaseInfo;

import java.util.List;

public interface IBinanceTickerService {

    void addTicker(String symbol, TickerBaseInfo ticker, long time);

    JSONObject getTicker(String coin, Long startTime, Integer rows);

    List<TickerBaseInfo> getDrawResult(String coin, Long betEndTime);

    /**
     * 清除数据
     *
     * @param retainSize
     */
    void cleanData(int retainSize, String coin);
}
