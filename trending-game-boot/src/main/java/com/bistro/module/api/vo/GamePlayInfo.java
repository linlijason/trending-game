package com.bistro.module.api.vo;

import java.math.BigDecimal;

public class GamePlayInfo {

    private Long id;
    private String betId;
    private String playTime;
    private String player;
    private BigDecimal betAmount;
    private BigDecimal payoutAmount;
    private BigDecimal multiplier;
    private GameBetDetail detail;
    private String currency;
    private String queryStart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(BigDecimal payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public GameBetDetail getDetail() {
        return detail;
    }

    public void setDetail(GameBetDetail detail) {
        this.detail = detail;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getQueryStart() {
        return queryStart;
    }

    public void setQueryStart(String queryStart) {
        this.queryStart = queryStart;
    }
}
