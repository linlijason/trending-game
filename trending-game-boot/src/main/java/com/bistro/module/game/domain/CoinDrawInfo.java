package com.bistro.module.game.domain;

import java.util.List;
import java.util.Map;

public class CoinDrawInfo {

    private String coin;
    private Long periodId;
    private List<PayoutInfo> payoutInfoList;
    private List<GameBet> gameBetList;
    private Map<String,String> userDrawResult;

    private Map coinPeriod;

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public List<PayoutInfo> getPayoutInfoList() {
        return payoutInfoList;
    }

    public void setPayoutInfoList(List<PayoutInfo> payoutInfoList) {
        this.payoutInfoList = payoutInfoList;
    }

    public Map getCoinPeriod() {
        return coinPeriod;
    }

    public void setCoinPeriod(Map coinPeriod) {
        this.coinPeriod = coinPeriod;
    }

    public List<GameBet> getGameBetList() {
        return gameBetList;
    }

    public void setGameBetList(List<GameBet> gameBetList) {
        this.gameBetList = gameBetList;
    }

    public Map<String, String> getUserDrawResult() {
        return userDrawResult;
    }

    public void setUserDrawResult(Map<String, String> userDrawResult) {
        this.userDrawResult = userDrawResult;
    }
}
