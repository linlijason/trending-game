package com.bistro.module.api.vo;

public class CoinBetCancelResponse {

    private String betId;//hash betid BetidHashUtils
    private Integer isCanceled;
    private String balance;
    private String currency;
    private String username;
    private String betAmount;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public Integer getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(Integer isCanceled) {
        this.isCanceled = isCanceled;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUsername() {
        return username;
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
}
