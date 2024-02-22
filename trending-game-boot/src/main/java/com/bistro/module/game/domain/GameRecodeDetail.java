package com.bistro.module.game.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 游戏记录详情对象 game_recode_detail
 * 
 * @author jason.lin
 * @date 2021-11-17
 */
public class GameRecodeDetail extends BaseEntity
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

    /** game_code */
    @Excel(name = "game_code")
    private String gameCode;

    /** 位置 */
    @Excel(name = "位置")
    private Integer index;

    /** 1 -砖石 0-雷  */
    @Excel(name = "1 -砖石 0-雷 ")
    private Integer shot;

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
    public void setIndex(Integer index) 
    {
        this.index = index;
    }

    public Integer getIndex() 
    {
        return index;
    }
    public void setShot(Integer shot) 
    {
        this.shot = shot;
    }

    public Integer getShot() 
    {
        return shot;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("betId", getBetId())
            .append("uid", getUid())
            .append("openId", getOpenId())
            .append("gameCode", getGameCode())
            .append("createTime", getCreateTime())
            .append("index", getIndex())
            .append("shot", getShot())
            .toString();
    }
}
