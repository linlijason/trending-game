package com.bistro.module.game.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 游戏记录对象 game_record_info
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public class GameBetRecordInfo
{

    private Long betId;
    private Long uid;
//    private String gameCode;
//    private String subGameCode;
    private String gameContent;
    private String roundNo;
    private Long periodId;
    private LocalDateTime drawTime;
    private String drawValue;
    private BigDecimal payoutAmount;
    private String extInfo;

    public Long getBetId() {
        return betId;
    }

    public void setBetId(Long betId) {
        this.betId = betId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

//    public String getGameCode() {
//        return gameCode;
//    }
//
//    public void setGameCode(String gameCode) {
//        this.gameCode = gameCode;
//    }
//
//    public String getSubGameCode() {
//        return subGameCode;
//    }
//
//    public void setSubGameCode(String subGameCode) {
//        this.subGameCode = subGameCode;
//    }

    public String getGameContent() {
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
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

    public LocalDateTime getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(LocalDateTime drawTime) {
        this.drawTime = drawTime;
    }

    public String getDrawValue() {
        return drawValue;
    }

    public void setDrawValue(String drawValue) {
        this.drawValue = drawValue;
    }

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(BigDecimal payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
}
