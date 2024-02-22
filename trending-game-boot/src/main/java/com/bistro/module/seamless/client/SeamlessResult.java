package com.bistro.module.seamless.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class SeamlessResult {

    @JSONField(name="is_success")
    @JsonProperty("is_success")
    private boolean isSuccess;

    @JSONField(name="err_msg")
    @JsonProperty("err_msg")
    private String errMsg;

    private String username;

    private BigDecimal balance;

    private String currency;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOriginalErrMsg() {
        return originalErrMsg;
    }

    public void setOriginalErrMsg(String originalErrMsg) {
        this.originalErrMsg = originalErrMsg;
    }
}
