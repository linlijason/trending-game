package com.bistro.module.game.domain;

import com.bistro.message.model.MessageUser;

public class PayoutInfo {
    private GameBet bet;
    private MessageUser user;

    public GameBet getBet() {
        return bet;
    }

    public void setBet(GameBet bet) {
        this.bet = bet;
    }

    public MessageUser getUser() {
        return user;
    }

    public void setUser(MessageUser user) {
        this.user = user;
    }
}
