package com.bistro.module.seamless.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BatchPayoutRequest {

    private long timestamp;

    @JSONField(name="merchant_code")
    @JsonProperty("merchant_code")
    private String merchantCode;


    private String sign;

    private List<Payout> payouts;


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public List<Payout> getPayouts() {
        return payouts;
    }

    public void setPayouts(List<Payout> payouts) {
        this.payouts = payouts;
    }

    public static class  Payout{
        @JSONField(name="unique_id")
        @JsonProperty("unique_id")
        private String uniqueId;

        private String username;//玩家用户名

        private String amount;//派彩金额

        private String currency;// 玩家货币

        @JSONField(name="game_code")
        @JsonProperty("game_code")
        private String gameCode;//游戏代码


        @JSONField(name="bet_id")
        @JsonProperty("bet_id")
        private long betId; //投注 id

        @JSONField(name="round_id")
        @JsonProperty("round_id")
        private String roundId;// 局号

        private String number;// 投注内容

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getGameCode() {
            return gameCode;
        }

        public void setGameCode(String gameCode) {
            this.gameCode = gameCode;
        }

        public long getBetId() {
            return betId;
        }

        public void setBetId(long betId) {
            this.betId = betId;
        }

        public String getRoundId() {
            return roundId;
        }

        public void setRoundId(String roundId) {
            this.roundId = roundId;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }
}
