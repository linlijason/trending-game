package com.bistro.module.payment.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 超时支付对象 game_payment_timeout
 * 
 * @author jason.lin
 * @date 2022-03-18
 */
public class GamePaymentTimeout extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** bet_id */
    @Excel(name = "bet_id")
    private Long betId;

    /** 1-bet 2-payout */
    @Excel(name = "1-bet 2-payout")
    private Integer paymentType;

    /** 处理状态 0 未处理 1 处理成功 2多次处理失败待人工处理 */
    @Excel(name = "处理状态 0 未处理 1 处理成功 2多次处理失败待人工处理")
    private Integer processStatus;

    /** 已处理次数 */
    @Excel(name = "已处理次数")
    private Long processCount;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merchantCode;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setBetId(Long betId) 
    {
        this.betId = betId;
    }

    public Long getBetId() 
    {
        return betId;
    }
    public void setPaymentType(Integer paymentType) 
    {
        this.paymentType = paymentType;
    }

    public Integer getPaymentType() 
    {
        return paymentType;
    }
    public void setProcessStatus(Integer processStatus) 
    {
        this.processStatus = processStatus;
    }

    public Integer getProcessStatus() 
    {
        return processStatus;
    }
    public void setProcessCount(Long processCount) 
    {
        this.processCount = processCount;
    }

    public Long getProcessCount() 
    {
        return processCount;
    }
    public void setMerchantCode(String merchantCode) 
    {
        this.merchantCode = merchantCode;
    }

    public String getMerchantCode() 
    {
        return merchantCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("betId", getBetId())
            .append("paymentType", getPaymentType())
            .append("processStatus", getProcessStatus())
            .append("processCount", getProcessCount())
            .append("createTime", getCreateTime())
            .append("merchantCode", getMerchantCode())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
