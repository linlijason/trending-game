package com.bistro.module.api.vo;

import java.util.List;

public class CoinMyBetResponse {
    private String betId;
    private List<CoinBetRequest.BetContent> drawResult;
    private String roundNo;
    private Long periodId;
    private String drawPrice;
    private Long drawEndTime;
    private String payoutAmount;
    private Long drawPriceTime;
    private String betPrice;
    private Long betTime;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public List<CoinBetRequest.BetContent> getDrawResult() {
        return drawResult;
    }

    public void setDrawResult(List<CoinBetRequest.BetContent> drawResult) {
        this.drawResult = drawResult;
    }

    public String getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(String roundNo) {
        this.roundNo = roundNo;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public String getDrawPrice() {
        return drawPrice;
    }

    public void setDrawPrice(String drawPrice) {
        this.drawPrice = drawPrice;
    }

    public Long getDrawEndTime() {
        return drawEndTime;
    }

    public void setDrawEndTime(Long drawEndTime) {
        this.drawEndTime = drawEndTime;
    }

    public String getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public Long getDrawPriceTime() {
        return drawPriceTime;
    }

    public void setDrawPriceTime(Long drawPriceTime) {
        this.drawPriceTime = drawPriceTime;
    }

    public String getBetPrice() {
        return betPrice;
    }

    public void setBetPrice(String betPrice) {
        this.betPrice = betPrice;
    }

    public Long getBetTime() {
        return betTime;
    }

    public void setBetTime(Long betTime) {
        this.betTime = betTime;
    }
}
