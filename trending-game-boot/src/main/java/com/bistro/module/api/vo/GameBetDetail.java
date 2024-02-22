package com.bistro.module.api.vo;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class GameBetDetail {

    private String betId;
    private JSONArray minesIndex;//类的位置
    private List clickedIndex;//已经扫过的位置

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public JSONArray getMinesIndex() {
        return minesIndex;
    }

    public void setMinesIndex(JSONArray minesIndex) {
        this.minesIndex = minesIndex;
    }

    public List getClickedIndex() {
        return clickedIndex;
    }

    public void setClickedIndex(List clickedIndex) {
        this.clickedIndex = clickedIndex;
    }
}
