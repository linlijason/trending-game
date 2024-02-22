package com.bistro.module.game.domain;

/**
 * 游戏记录对象 game_record_info
 *
 * @author jason.lin
 * @date 2021-11-17
 */
public class GameBetRecordQuery {

    private Long uid;
    private String gameCode;
    private String subGameCode;
    private Integer rows;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getSubGameCode() {
        return subGameCode;
    }

    public void setSubGameCode(String subGameCode) {
        this.subGameCode = subGameCode;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
