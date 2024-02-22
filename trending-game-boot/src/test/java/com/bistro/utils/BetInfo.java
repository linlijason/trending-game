package com.bistro.utils;

import com.bistro.common.annotation.Excel;

import java.util.Date;

public class BetInfo {



    @Excel(name = "bet_id")
    private String betId;

    /** 用户账号 */
    @Excel(name = "user_name")
    private String userName;

    @Excel(name = "bet_amount")
    private String bet_amount;

    @Excel(name = "payout_amount")
    private String payout_amount;

    @Excel(name = "status")
    private String status;

    @Excel(name = "game_content")
    private String gameContent;

    @Excel(name = "bet_time")
    private Date betTime;

    @Excel(name = "bet_start_time")
    private Date betStartTime;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBet_amount() {
        return bet_amount;
    }

    public void setBet_amount(String bet_amount) {
        this.bet_amount = bet_amount;
    }

    public String getPayout_amount() {
        return payout_amount;
    }

    public void setPayout_amount(String payout_amount) {
        this.payout_amount = payout_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGameContent() {
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
    }

    public Date getBetTime() {
        return betTime;
    }

    public void setBetTime(Date betTime) {
        this.betTime = betTime;
    }

    public Date getBetStartTime() {
        return betStartTime;
    }

    public void setBetStartTime(Date betStartTime) {
        this.betStartTime = betStartTime;
    }
}
