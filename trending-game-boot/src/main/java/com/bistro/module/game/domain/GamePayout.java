package com.bistro.module.game.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 提现对象 game_payout
 * 
 * @author jason.lin
 * @date 2021-11-23
 */
public class GamePayout extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键")
    private Long id;

    /** uid */
    @Excel(name = "uid")
    private Long uid;

    /** open id */
    @Excel(name = "open id")
    private String openId;

    /** bet id */
    @Excel(name = "bet id")
    private Long betId;

    /** game code */
    @Excel(name = "game code")
    private String gameCode;

    /** 提现金额 */
    @Excel(name = "提现金额")
    private BigDecimal amount;

    /** 倍数 */
    @Excel(name = "倍数")
    private BigDecimal multiplier;

    /** 货币 */
    @Excel(name = "货币")
    private String currency;

    @Excel(name = "sub game code")
    private String subGameCode;

    /** 对应期数 */
    @Excel(name = "对应期数")
    private Long periodId;


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
    public void setBetId(Long betId) 
    {
        this.betId = betId;
    }

    public Long getBetId() 
    {
        return betId;
    }
    public void setGameCode(String gameCode) 
    {
        this.gameCode = gameCode;
    }

    public String getGameCode() 
    {
        return gameCode;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setMultiplier(BigDecimal multiplier) 
    {
        this.multiplier = multiplier;
    }

    public BigDecimal getMultiplier() 
    {
        return multiplier;
    }
    public void setCurrency(String currency) 
    {
        this.currency = currency;
    }

    public String getCurrency() 
    {
        return currency;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("openId", getOpenId())
            .append("betId", getBetId())
            .append("gameCode", getGameCode())
            .append("amount", getAmount())
            .append("multiplier", getMultiplier())
            .append("currency", getCurrency())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
