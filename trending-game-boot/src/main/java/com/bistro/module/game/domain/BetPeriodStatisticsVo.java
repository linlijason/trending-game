package com.bistro.module.game.domain;

import java.math.BigDecimal;

public class BetPeriodStatisticsVo {

    private String period;
    private int betCount;
    private int payoutCount;
    //private int playerCount;
    private BigDecimal betAmountTotal;
    private BigDecimal payoutAmountTotal;
    private BigDecimal gross;//毛利 betAmountTotal-payoutAmountTotal
    private BigDecimal grossProfit;//毛利率
    private Long uid;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getBetCount() {
        return betCount;
    }

    public void setBetCount(int betCount) {
        this.betCount = betCount;
    }

    public int getPayoutCount() {
        return payoutCount;
    }

    public void setPayoutCount(int payoutCount) {
        this.payoutCount = payoutCount;
    }

    public BigDecimal getBetAmountTotal() {
        return betAmountTotal;
    }

    public void setBetAmountTotal(BigDecimal betAmountTotal) {
        this.betAmountTotal = betAmountTotal;
    }

    public BigDecimal getPayoutAmountTotal() {
        return payoutAmountTotal;
    }

    public void setPayoutAmountTotal(BigDecimal payoutAmountTotal) {
        this.payoutAmountTotal = payoutAmountTotal;
    }

    public BigDecimal getGross() {
        return gross;
    }

    public void setGross(BigDecimal gross) {
        this.gross = gross;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
