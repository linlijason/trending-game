package com.bistro.module.game.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 奖期管理对象 game_period
 * 
 * @author jason.lin
 * @date 2021-12-30
 */
public class GamePeriod extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 游戏编码 */
    @Excel(name = "游戏编码")
    private String gameCode;

    /** 游戏子编码 */
    @Excel(name = "游戏子编码")
    private String subGameCode;

    /** 期号 */
    @Excel(name = "期号")
    private String roundNo;

    /** 投注开始时间 */
    private LocalDateTime betStartTime;

    /** 投注结束时间 */
    private LocalDateTime betEndTime;

    /** 开奖时间 */
    @Excel(name = "开奖时间")
    private LocalDateTime drawTime;

    /** 开奖值 */
    @Excel(name = "开奖值")
    private String drawValue;

    /** 开奖结果 */
    @Excel(name = "开奖结果")
    private String drawResult;

    /** 订单数量 */
    @Excel(name = "订单数量")
    private Long orderCount;

    /** 收入 */
    @Excel(name = "收入")
    private BigDecimal income;

    /** 扩展信息 */
    private String extInfo;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setGameCode(String gameCode) 
    {
        this.gameCode = gameCode;
    }

    public String getGameCode() 
    {
        return gameCode;
    }
    public void setSubGameCode(String subGameCode) 
    {
        this.subGameCode = subGameCode;
    }

    public String getSubGameCode() 
    {
        return subGameCode;
    }
    public void setRoundNo(String roundNo) 
    {
        this.roundNo = roundNo;
    }

    public String getRoundNo() 
    {
        return roundNo;
    }
    public void setBetStartTime(LocalDateTime betStartTime) 
    {
        this.betStartTime = betStartTime;
    }

    public LocalDateTime getBetStartTime() 
    {
        return betStartTime;
    }
    public void setBetEndTime(LocalDateTime betEndTime) 
    {
        this.betEndTime = betEndTime;
    }

    public LocalDateTime getBetEndTime() 
    {
        return betEndTime;
    }
    public void setDrawTime(LocalDateTime drawTime) 
    {
        this.drawTime = drawTime;
    }

    public LocalDateTime getDrawTime() 
    {
        return drawTime;
    }
    public void setDrawValue(String drawValue) 
    {
        this.drawValue = drawValue;
    }

    public String getDrawValue() 
    {
        return drawValue;
    }
    public void setDrawResult(String drawResult) 
    {
        this.drawResult = drawResult;
    }

    public String getDrawResult() 
    {
        return drawResult;
    }
    public void setOrderCount(Long orderCount) 
    {
        this.orderCount = orderCount;
    }

    public Long getOrderCount() 
    {
        return orderCount;
    }
    public void setIncome(BigDecimal income) 
    {
        this.income = income;
    }

    public BigDecimal getIncome() 
    {
        return income;
    }
    public void setExtInfo(String extInfo) 
    {
        this.extInfo = extInfo;
    }

    public String getExtInfo() 
    {
        return extInfo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gameCode", getGameCode())
            .append("subGameCode", getSubGameCode())
            .append("roundNo", getRoundNo())
            .append("betStartTime", getBetStartTime())
            .append("betEndTime", getBetEndTime())
            .append("drawTime", getDrawTime())
            .append("drawValue", getDrawValue())
            .append("drawResult", getDrawResult())
            .append("orderCount", getOrderCount())
            .append("income", getIncome())
            .append("extInfo", getExtInfo())
            .toString();
    }
}
