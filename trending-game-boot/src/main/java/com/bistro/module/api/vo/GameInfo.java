package com.bistro.module.api.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.List;

public class GameInfo {

    private Limit limit;
    private List<MultiplierConfigItem> multiplierConfig;
    private String gameCode;
    private List<CurrencyVo> currency;
    private List<Limit> limits;

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public List<MultiplierConfigItem> getMultiplierConfig() {
        return multiplierConfig;
    }

    public void setMultiplierConfig(List<MultiplierConfigItem> multiplierConfig) {
        this.multiplierConfig = multiplierConfig;
    }

    public List<CurrencyVo> getCurrency() {
        return currency;
    }

    public void setCurrency(List<CurrencyVo> currency) {
        this.currency = currency;
    }


    public List<Limit> getLimits() {
        return limits;
    }

    public void setLimits(List<Limit> limits) {
        this.limits = limits;
    }

    public static class Limit {
        private BigDecimal minBetAmount;//下注的最小金额
        private BigDecimal maxBetAmount;//下注的最大金额
        private BigDecimal maxPayoutAmount;//赢得的最大金额
        private String currencyCode;

        public BigDecimal getMinBetAmount() {
            return minBetAmount;
        }

        public void setMinBetAmount(BigDecimal minBetAmount) {
            this.minBetAmount = minBetAmount;
        }

        public BigDecimal getMaxBetAmount() {
            return maxBetAmount;
        }

        public void setMaxBetAmount(BigDecimal maxBetAmount) {
            this.maxBetAmount = maxBetAmount;
        }

        public BigDecimal getMaxPayoutAmount() {
            return maxPayoutAmount;
        }

        public void setMaxPayoutAmount(BigDecimal maxPayoutAmount) {
            this.maxPayoutAmount = maxPayoutAmount;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }
    }


    public static class MultiplierConfigItem {
        private int minesCount;//雷的数量
        private List<MultiplierHits> multiplierList;//选择上面的雷数量之后对应的命中钻石及相应的赔率

        public int getMinesCount() {
            return minesCount;
        }

        public void setMinesCount(int minesCount) {
            this.minesCount = minesCount;
        }

        public List<MultiplierHits> getMultiplierList() {
            return multiplierList;
        }

        public void setMultiplierList(List<MultiplierHits> multiplierList) {
            this.multiplierList = multiplierList;
        }
    }


    public static class MultiplierHits {
        private int hits;
        private BigDecimal multiplier;//如果设置了人工则用人工赔率，否则用默认赔率. 对外展示统一用这个
        @JSONField(serialize =false)
        private BigDecimal multiplier2; //人工赔率，人工不指定就是null
        @JSONField(serialize =false)
        private BigDecimal defaultMultiplier;//默认赔率

        public int getHits() {
            return hits;
        }

        public void setHits(int hits) {
            this.hits = hits;
        }

        public BigDecimal getMultiplier() {
            if (multiplier2 != null) {
                return multiplier2;
            }
            return multiplier;
        }

        public void setMultiplier(BigDecimal multiplier) {
            this.multiplier = multiplier;
        }

        public BigDecimal getMultiplier2() {
            return multiplier2;
        }

        public void setMultiplier2(BigDecimal multiplier2) {
            this.multiplier2 = multiplier2;
        }

        public BigDecimal getDefaultMultiplier() {
            return defaultMultiplier;
        }

        public void setDefaultMultiplier(BigDecimal defaultMultiplier) {
            this.defaultMultiplier = defaultMultiplier;
        }
    }


}
