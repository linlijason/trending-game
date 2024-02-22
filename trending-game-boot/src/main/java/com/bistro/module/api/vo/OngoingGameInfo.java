package com.bistro.module.api.vo;

import java.math.BigDecimal;
import java.util.List;

public class OngoingGameInfo {
    private String betId;//下注时的id
    private BigDecimal amount;//当前赢得的金额
    private List<Integer> diamondIndex;  //已经命中的钻石位置
    private String currency;
    private BigDecimal betAmount;//下注金额
    private Integer minesCount;//雷数量

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<Integer> getDiamondIndex() {
        return diamondIndex;
    }

    public void setDiamondIndex(List<Integer> diamondIndex) {
        this.diamondIndex = diamondIndex;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public Integer getMinesCount() {
        return minesCount;
    }

    public void setMinesCount(Integer minesCount) {
        this.minesCount = minesCount;
    }
}
