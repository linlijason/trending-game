package com.bistro.module.player.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 玩家信息对象 game_user_info
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public class GameUserInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键")
    private Long id;

    /** open id */
    @Excel(name = "open id")
    private String openId;

    /** 接入方code */
    @Excel(name = "接入方code")
    private String fromCode;

    /** name */
    @Excel(name = "name")
    private String name;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setOpenId(String openId) 
    {
        this.openId = openId;
    }

    public String getOpenId() 
    {
        return openId;
    }
    public void setFromCode(String fromCode) 
    {
        this.fromCode = fromCode;
    }

    public String getFromCode() 
    {
        return fromCode;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("openId", getOpenId())
            .append("fromCode", getFromCode())
            .append("name", getName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
