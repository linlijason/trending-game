package com.bistro.module.merchant.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 商户信息对象 merchant_info
 *
 * @author jason.lin
 * @date 2021-11-23
 */
public class MerchantInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merchantCode;

    /** 安全密钥 */
    @Excel(name = "安全密钥")
    private String secureKey;

    /** 签名key */
    @Excel(name = "签名key")
    private String signKey;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setMerchantCode(String merchantCode)
    {
        this.merchantCode = merchantCode;
    }

    public String getMerchantCode()
    {
        return merchantCode;
    }
    public void setSecureKey(String secureKey)
    {
        this.secureKey = secureKey;
    }

    public String getSecureKey()
    {
        return secureKey;
    }
    public void setSignKey(String signKey)
    {
        this.signKey = signKey;
    }

    public String getSignKey()
    {
        return signKey;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("merchantCode", getMerchantCode())
                .append("secureKey", getSecureKey())
                .append("signKey", getSignKey())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}