package com.bistro.module.seamless.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundRequest extends BaseRequest{

    private String username;//玩家用户名

    private String amount;//投注金额

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

    private int type;// 投注内容  1: refund 2: payout failed 3:issue cancel 1:退回 2:派彩失败 3:取消

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
