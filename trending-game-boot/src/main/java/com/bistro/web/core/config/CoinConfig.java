package com.bistro.web.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "coin")
public class CoinConfig {


    private List<String> supports;

    private Map<String, String> stream;

    private Map<String, String> symbol;

    @Value("${coin.duration.bet}")
    private Integer betDuration;

    @Value("${coin.duration.betReal}")
    private Integer betDurationReal;

    @Value("${coin.duration.draw}")
    private Integer drawDuration;

    @Value("${coin.batchPayout}")
    private Boolean batchPayout;

    @Value("${coin.batchPayoutGroupSize}")
    private int batchPayoutGroupSize;

    @Value("${coin.drawTimeContinuityCheck}")
    private Boolean drawTimeContinuityCheck;

    @Value("${coin.goTieRefundMultiplier}")
    private BigDecimal goTieRefundMultiplier;


    public List<String> getSupports() {
        return supports;
    }

    public void setSupports(List<String> supports) {
        this.supports = supports;
    }

    public Map<String, String> getStream() {
        return stream;
    }

    public void setStream(Map<String, String> stream) {
        this.stream = stream;
    }

    public Map<String, String> getSymbol() {
        return symbol;
    }

    public void setSymbol(Map<String, String> symbol) {
        this.symbol = symbol;
    }

    public Integer getBetDuration() {
        return betDuration;
    }

    public void setBetDuration(Integer betDuration) {
        this.betDuration = betDuration;
    }

    public Integer getDrawDuration() {
        return drawDuration;
    }

    public void setDrawDuration(Integer drawDuration) {
        this.drawDuration = drawDuration;
    }

    public Boolean getBatchPayout() {
        return batchPayout;
    }

    public void setBatchPayout(Boolean batchPayout) {
        this.batchPayout = batchPayout;
    }

    public int getBatchPayoutGroupSize() {
        return batchPayoutGroupSize;
    }

    public void setBatchPayoutGroupSize(int batchPayoutGroupSize) {
        this.batchPayoutGroupSize = batchPayoutGroupSize;
    }

    public Boolean getDrawTimeContinuityCheck() {
        return drawTimeContinuityCheck;
    }

    public void setDrawTimeContinuityCheck(Boolean drawTimeContinuityCheck) {
        this.drawTimeContinuityCheck = drawTimeContinuityCheck;
    }

    public Integer getBetDurationReal() {
        return betDurationReal;
    }

    public void setBetDurationReal(Integer betDurationReal) {
        this.betDurationReal = betDurationReal;
    }

    public BigDecimal getGoTieRefundMultiplier() {
        return goTieRefundMultiplier;
    }

    public void setGoTieRefundMultiplier(BigDecimal goTieRefundMultiplier) {
        this.goTieRefundMultiplier = goTieRefundMultiplier;
    }
}




