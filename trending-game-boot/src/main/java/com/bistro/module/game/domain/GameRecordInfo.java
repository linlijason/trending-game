package com.bistro.module.game.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 游戏记录对象 game_record_info
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public class GameRecordInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** bet id */
    @Excel(name = "bet id")
    private Long betId;

    /** uid */
    @Excel(name = "uid")
    private Long uid;

    /** open id */
    @Excel(name = "open id")
    private String openId;

    /** game code */
    @Excel(name = "game code")
    private String gameCode;

    /** 游戏内容信息 */
    @Excel(name = "游戏内容信息")
    private String gameContent;

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
    public void setGameContent(String gameContent) 
    {
        this.gameContent = gameContent;
    }

    public String getGameContent() 
    {
        return gameContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("betId", getBetId())
            .append("uid", getUid())
            .append("openId", getOpenId())
            .append("gameCode", getGameCode())
            .append("gameContent", getGameContent())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
