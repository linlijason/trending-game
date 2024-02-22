package com.bistro.module.game.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 货币配置对象 currency_info
 * 
 * @author jason.lin
 * @date 2021-12-06
 */
public class CurrencyInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 货币编码 */
    @Excel(name = "货币编码")
    private String currencyCode;

    /** 货币名称 */
    @Excel(name = "货币名称")
    private String currencyName;

    /** 货币图片 */
    @Excel(name = "货币图片")
    private String currencyIcon;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setCurrencyCode(String currencyCode) 
    {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() 
    {
        return currencyCode;
    }
    public void setCurrencyName(String currencyName) 
    {
        this.currencyName = currencyName;
    }

    public String getCurrencyName() 
    {
        return currencyName;
    }
    public void setCurrencyIcon(String currencyIcon) 
    {
        this.currencyIcon = currencyIcon;
    }

    public String getCurrencyIcon() 
    {
        return currencyIcon;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("currencyCode", getCurrencyCode())
            .append("currencyName", getCurrencyName())
            .append("currencyIcon", getCurrencyIcon())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
