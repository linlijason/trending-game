package com.bistro.module.game.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.constants.Constants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BetGameUserVo {
    @Excel(name = "betId")
    private long betId;
    @Excel(name = "gameCode")
    private String gameCode;
    @Excel(name = "gameName")
    private String gameName;
    @Excel(name = "uid")
    private long uid;
    @Excel(name = "userName")
    private String userName;
    @Excel(name = "merchantCode")
    private String merchantCode;
    @Excel(name = "betAmount")
    private BigDecimal betAmount;
    @Excel(name = "multiplier")
    private BigDecimal multiplier;
    @Excel(name = "payoutAmount")
    private BigDecimal payoutAmount;
    @Excel(name = "currency")
    private String currency;
    @Excel(name = "status")
    private short status; //1 -初始 2-下注扣款完成 3- 下注扣款失败 4-提现成功 5-提现失败。6游戏爆了
    private String gameContent;
    @Excel(name = "betTime")
    private LocalDateTime betTime;
    @Excel(name = "subGameCode")
    private String subGameCode;

    public long getBetId() {
        return betId;
    }

    public void setBetId(long betId) {
        this.betId = betId;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(BigDecimal payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public String getGameContent() {
        if (Constants.MINES_GAME_CODE.equals(gameCode)) {
            return null;
        }
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
    }

    public LocalDateTime getBetTime() {
        return betTime;
    }

    public void setBetTime(LocalDateTime betTime) {
        this.betTime = betTime;
    }

    public String getSubGameCode() {
        return subGameCode;
    }

    public void setSubGameCode(String subGameCode) {
        this.subGameCode = subGameCode;
    }
}
