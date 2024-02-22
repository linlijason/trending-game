package com.bistro.message.model;

import com.bistro.module.game.domain.GameBet;

import java.util.List;

public class WithdrawMessage {

    private Request request;

    private Response response;


    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Request {
        private Integer type; //1 手动，2 自动
        private String betId;
        private GameBet bet;//type为2的时候传过来，不用在查一次库
        private List<Integer> index;

        public String getBetId() {
            return betId;
        }

        public void setBetId(String betId) {
            this.betId = betId;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public GameBet getBet() {
            return bet;
        }

        public void setBet(GameBet bet) {
            this.bet = bet;
        }

        public List<Integer> getIndex() {
            return index;
        }

        public void setIndex(List<Integer> index) {
            this.index = index;
        }
    }

    public static class Response {
        private List<Integer> index;//雷的位置
        private String balance;
        private String currency;
        private Integer isPayed;

        public List<Integer> getIndex() {
            return index;
        }

        public void setIndex(List<Integer> index) {
            this.index = index;
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

        public Integer getIsPayed() {
            return isPayed;
        }

        public void setIsPayed(Integer isPayed) {
            this.isPayed = isPayed;
        }
    }
}
