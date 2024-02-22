package com.bistro.module.game.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 下注信息对象 game_bet
 * 
 * @author gavin
 * @date 2021-11-23
 */
public class GameBet extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键")
    private Long id;

    /** uid */
    @Excel(name = "uid")
    private Long uid;

    /** openid */
    @Excel(name = "openid")
    private String openId;

    /** game code */
    @Excel(name = "game code")
    private String gameCode;

    /** bet amount */
    @Excel(name = "bet amount")
    private BigDecimal betAmount;

    /** 倍数/赔率 */
    @Excel(name = "倍数/赔率")
    private BigDecimal multiplier;

    /** payout amount */
    @Excel(name = "payout amount")
    private BigDecimal payoutAmount;

    /** 1 -初始 2-下注扣款完成 3- 下注扣款失败 4-提现成功（包含payout=0）5-提现失败 */
    @Excel(name = "1 -初始 2-下注扣款完成 3- 下注扣款失败 4-提现成功", readConverterExp = "包=含payout=0")
    private Integer status;

    /** 货币 */
    @Excel(name = "货币")
    private String currency;

    /** 玩家名称 */
    @Excel(name = "玩家名称")
    private String userName;

    @Excel(name = "sub game code")
    private String subGameCode;

    /** 对应期数 */
    @Excel(name = "对应期数")
    private Long periodId;

    /** 期数编号 */
    @Excel(name = "期数编号")
    private String roundNo;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUid(Long uid) 
    {
        this.uid = uid;
    }

    public Long getUid() 
    {
        return uid;
    }
    public void setOpenId(String openId) 
    {
        this.openId = openId;
    }

    public String getOpenId() 
    {
        return openId;
    }
    public void setGameCode(String gameCode) 
    {
        this.gameCode = gameCode;
    }

    public String getGameCode() 
    {
        return gameCode;
    }
    public void setBetAmount(BigDecimal betAmount) 
    {
        this.betAmount = betAmount;
    }

    public BigDecimal getBetAmount() 
    {
        return betAmount;
    }
    public void setMultiplier(BigDecimal multiplier) 
    {
        this.multiplier = multiplier;
    }

    public BigDecimal getMultiplier() 
    {
        return multiplier;
    }
    public void setPayoutAmount(BigDecimal payoutAmount) 
    {
        this.payoutAmount = payoutAmount;
    }

    public BigDecimal getPayoutAmount() 
    {
        return payoutAmount;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setCurrency(String currency) 
    {
        this.currency = currency;
    }

    public String getCurrency() 
    {
        return currency;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public String getSubGameCode() {
        return subGameCode;
    }

    public void setSubGameCode(String subGameCode) {
        this.subGameCode = subGameCode;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public String getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(String roundNo) {
        this.roundNo = roundNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("openId", getOpenId())
            .append("gameCode", getGameCode())
            .append("betAmount", getBetAmount())
            .append("multiplier", getMultiplier())
            .append("payoutAmount", getPayoutAmount())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("currency", getCurrency())
            .append("userName", getUserName())
            .toString();
    }
}
