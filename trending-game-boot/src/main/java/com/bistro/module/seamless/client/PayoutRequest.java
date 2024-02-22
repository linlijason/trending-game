package com.bistro.module.seamless.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PayoutRequest extends BaseRequest{

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
