package com.bistro.module.seamless.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.bistro.module.game.domain.PayoutInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class SeamlessBatchPayoutResult {

    @JSONField(name = "is_success")
    @JsonProperty("is_success")
    private boolean isSuccess;

    @JSONField(name = "err_msg")
    @JsonProperty("err_msg")
    private String errMsg;

    private List<Payout> payouts;

    @JSONField(serialize = false)
    private Map<String, PayoutInfo> payoutInfos;

    @JSONField(serialize = false)
    private String originalErrMsg;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public List<Payout> getPayouts() {
        return payouts;
    }

    public void setPayouts(List<Payout> payouts) {
        this.payouts = payouts;
    }

    public Map<String, PayoutInfo> getPayoutInfos() {
        return payoutInfos;
    }

    public void setPayoutInfos(Map<String, PayoutInfo> payoutInfos) {
        this.payoutInfos = payoutInfos;
    }

    public String getOriginalErrMsg() {
        return originalErrMsg;
    }

    public void setOriginalErrMsg(String originalErrMsg) {
        this.originalErrMsg = originalErrMsg;
    }

    public static class Payout {
        @JSONField(name = "unique_id")
        @JsonProperty("unique_id")
        private String uniqueId;

        @JSONField(name = "is_success")
        @JsonProperty("is_success")
        private boolean isSuccess;

        private String username;//玩家用户名

        private String balance;//余额

        private String currency;// 玩家货币

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
