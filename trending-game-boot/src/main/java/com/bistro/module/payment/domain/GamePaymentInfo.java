package com.bistro.module.payment.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 支付信息对象 game_payment_info
 * 
 * @author gavin
 * @date 2021-11-23
 */
public class GamePaymentInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** uid */
    @Excel(name = "uid")
    private Long uid;

    /** open id */
    @Excel(name = "open id")
    private String openId;

    /** 支付单号 */
    @Excel(name = "支付单号")
    private String paymentOrderNo;

    /** 业务id */
    @Excel(name = "业务id")
    private Long businessId;

    /** 业务类型 1 - 下注扣款 2- 提现打款 */
    @Excel(name = "业务类型 1 - 下注扣款 2- 提现打款 3-退回投注")
    private Integer businsesType;

    /** 支付渠道 */
    @Excel(name = "支付渠道")
    private String paymentChannel;

    /** 渠道单号 */
    @Excel(name = "渠道单号")
    private String chanelOrderNo;

    /** 支付金额 */
    @Excel(name = "支付金额")
    private BigDecimal amount;

    /** 货币 */
    @Excel(name = "货币")
    private String currency;

    /** 支付状态 1 - 初始 2 -成功 3 -失败 */
    @Excel(name = "支付状态 1 - 初始 2 -成功 3 -失败")
    private Integer status;

    /** 支付接口内容 */
    @Excel(name = "支付接口内容")
    private String content;

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
    public void setPaymentOrderNo(String paymentOrderNo) 
    {
        this.paymentOrderNo = paymentOrderNo;
    }

    public String getPaymentOrderNo() 
    {
        return paymentOrderNo;
    }
    public void setBusinessId(Long businessId) 
    {
        this.businessId = businessId;
    }

    public Long getBusinessId() 
    {
        return businessId;
    }
    public void setBusinsesType(Integer businsesType) 
    {
        this.businsesType = businsesType;
    }

    public Integer getBusinsesType() 
    {
        return businsesType;
    }
    public void setPaymentChannel(String paymentChannel) 
    {
        this.paymentChannel = paymentChannel;
    }

    public String getPaymentChannel() 
    {
        return paymentChannel;
    }
    public void setChanelOrderNo(String chanelOrderNo) 
    {
        this.chanelOrderNo = chanelOrderNo;
    }

    public String getChanelOrderNo() 
    {
        return chanelOrderNo;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setCurrency(String currency) 
    {
        this.currency = currency;
    }

    public String getCurrency() 
    {
        return currency;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("openId", getOpenId())
            .append("paymentOrderNo", getPaymentOrderNo())
            .append("businessId", getBusinessId())
            .append("businsesType", getBusinsesType())
            .append("paymentChannel", getPaymentChannel())
            .append("chanelOrderNo", getChanelOrderNo())
            .append("amount", getAmount())
            .append("createTime", getCreateTime())
            .append("currency", getCurrency())
            .append("updateTime", getUpdateTime())
            .append("status", getStatus())
            .append("content", getContent())
            .toString();
    }
}
