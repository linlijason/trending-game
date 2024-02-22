package com.bistro.module.api.vo;

public class GamePlayRequest {


    private Integer type;//1 -按payout 从高到第， 按multiplier从高到低
    private Long uid;
    private String gameCode;
    private String order;
    private Integer rows;
    private String queryStart;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getQueryStart() {
        return queryStart;
    }

    public void setQueryStart(String queryStart) {
        this.queryStart = queryStart;
    }
}
