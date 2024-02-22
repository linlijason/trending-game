package com.bistro.message.model;


import java.util.List;

public class PlayMessage {

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

        private List<Integer> indexes;
        private String betId;
        private int type;//1手动 2自动

        public List<Integer> getIndexes() {
            return indexes;
        }

        public void setIndexes(List<Integer> indexes) {
            this.indexes = indexes;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getBetId() {
            return betId;
        }

        public void setBetId(String betId) {
            this.betId = betId;
        }
    }

    public static class Response {

        private List<Integer> index;
        private String amount;
        private int win;//0输 1赢
        private String balance;
        private String currency;
        private Integer isPayed;


        public List<Integer> getIndex() {
            return index;
        }

        public void setIndex(List<Integer> index) {
            this.index = index;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getWin() {
            return win;
        }

        public void setWin(int win) {
            this.win = win;
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
