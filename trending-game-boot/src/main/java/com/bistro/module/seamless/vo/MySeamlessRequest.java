package com.bistro.module.seamless.vo;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MySeamlessRequest {

    @JsonProperty("merchant_code")
    @JSONField(name="merchant_code")
    private String merchantCode;

    @JsonProperty("secure_key")
    @JSONField(name="secure_key")
    private String secureKey;

    private String sign;

    @JsonProperty("auth_token")
    @JSONField(name="auth_token")
    private String authToken;

    private String username;

    @JsonProperty("game_code")
    @JSONField(name="game_code")
    private String gameCode;

    private String token;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getSecureKey() {
        return secureKey;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
