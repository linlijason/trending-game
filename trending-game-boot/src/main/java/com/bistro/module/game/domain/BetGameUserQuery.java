package com.bistro.module.game.domain;


import java.time.LocalDateTime;

public class BetGameUserQuery {
    private String gameCode;
    private String subGameCode;
    private LocalDateTime beginCreateTime;
    private LocalDateTime endCreateTime;
    private String userName;
    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getSubGameCode() {
        return subGameCode;
    }

    public void setSubGameCode(String subGameCode) {
        this.subGameCode = subGameCode;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
