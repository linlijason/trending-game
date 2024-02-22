package com.bistro.module.game.domain;

import java.math.BigDecimal;

public class CoinGuessOptionConfig {
    private CoinGuessOption option;
    private BigDecimal odds;
    private BigDecimal originOdds;

    public BigDecimal getOriginOdds() {
        return originOdds;
    }

    public void setOriginOdds(BigDecimal originOdds) {
        this.originOdds = originOdds;
    }

    public CoinGuessOption getOption() {
        return option;
    }

    public void setOption(CoinGuessOption option) {
        this.option = option;
    }

    public BigDecimal getOdds() {
        return odds;
    }

    public void setOdds(BigDecimal odds) {
        this.odds = odds;
    }
}
