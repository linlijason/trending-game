package com.bistro.module.api.vo;

public class CoinMyBetRequest {
    private String coin;
    private Integer rows;

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
