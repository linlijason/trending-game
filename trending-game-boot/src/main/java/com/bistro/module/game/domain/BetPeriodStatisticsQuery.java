package com.bistro.module.game.domain;

import java.time.LocalDateTime;

public class BetPeriodStatisticsQuery {
    private LocalDateTime beginCreateTime;
    private LocalDateTime endCreateTime;
    private String gameCode;
    private String merchantCode;
    private String subGameCode;

    public LocalDateTime getBeginCreateTime() {
        return beginCreateTime;
    }

    public void setBeginCreateTime(LocalDateTime beginCreateTime) {
        this.beginCreateTime = beginCreateTime;
    }

    public LocalDateTime getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(LocalDateTime endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getSubGameCode() {
        return subGameCode;
    }

    public void setSubGameCode(String subGameCode) {
        this.subGameCode = subGameCode;
    }
}
