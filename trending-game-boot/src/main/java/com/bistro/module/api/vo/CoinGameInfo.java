package com.bistro.module.api.vo;

import com.bistro.module.game.domain.CoinGuessOptionConfig;

import java.util.List;

public class CoinGameInfo {

    private GameInfo.Limit limit;
    private List<CurrencyVo> currency;
    private List<GameInfo.Limit> limits;
    private String defaultBetAmount;
    private List<CoinGuessOptionConfig> options;
    private List<String> coins;
    private Integer betDuration;//投注时间
    private Integer drawDuration;//开奖时间

    public GameInfo.Limit getLimit() {
        return limit;
    }

    public void setLimit(GameInfo.Limit limit) {
        this.limit = limit;
    }

    public List<CurrencyVo> getCurrency() {
        return currency;
    }

    public void setCurrency(List<CurrencyVo> currency) {
        this.currency = currency;
    }

    public List<GameInfo.Limit> getLimits() {
        return limits;
    }

    public void setLimits(List<GameInfo.Limit> limits) {
        this.limits = limits;
    }

    public String getDefaultBetAmount() {
        return defaultBetAmount;
    }

    public void setDefaultBetAmount(String defaultBetAmount) {
        this.defaultBetAmount = defaultBetAmount;
    }

    public List<CoinGuessOptionConfig> getOptions() {
        return options;
    }

    public void setOptions(List<CoinGuessOptionConfig> options) {
        this.options = options;
    }

    public List<String> getCoins() {
        return coins;
    }

    public void setCoins(List<String> coins) {
        this.coins = coins;
    }

    public Integer getBetDuration() {
        return betDuration;
    }

    public void setBetDuration(Integer betDuration) {
        this.betDuration = betDuration;
    }

    public Integer getDrawDuration() {
        return drawDuration;
    }

    public void setDrawDuration(Integer drawDuration) {
        this.drawDuration = drawDuration;
    }
}
