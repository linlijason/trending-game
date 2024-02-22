package com.bistro.module.game.domain.ext;

public class MineAndHitConfigDto {
    private String minesCount;
    private String hits;
    private String multiplier2;

    public String getMinesCount() {
        return minesCount;
    }

    public void setMinesCount(String minesCount) {
        this.minesCount = minesCount;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getMultiplier2() {
        return multiplier2;
    }

    public void setMultiplier2(String multiplier2) {
        this.multiplier2 = multiplier2;
    }
}
