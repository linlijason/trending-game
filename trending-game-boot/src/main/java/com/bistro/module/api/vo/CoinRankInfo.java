package com.bistro.module.api.vo;

import com.bistro.module.game.domain.CoinGuessOption;

import java.util.List;

public class CoinRankInfo {

    private String username;
    private String betAmount;
    private List<CoinGuessOption> options;
    private String payoutAmount;
    private String betId;

    public String getUsername() {
        return new StringBuilder(username).replace(1, username.length() - 1, "****").toString();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public List<CoinGuessOption> getOptions() {
        return options;
    }

    public void setOptions(List<CoinGuessOption> options) {
        this.options = options;
    }

    public String getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }
}
