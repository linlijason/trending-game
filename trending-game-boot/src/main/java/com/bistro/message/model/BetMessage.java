package com.bistro.message.model;

public class BetMessage {

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

        private String gameCode;
        private int minesCount;//选择雷的数量
        private String betAmount;
        private String currency;

        public String getGameCode() {
            return gameCode;
        }

        public void setGameCode(String gameCode) {
            this.gameCode = gameCode;
        }

        public int getMinesCount() {
            return minesCount;
        }

        public void setMinesCount(int minesCount) {
            this.minesCount = minesCount;
        }

        public String getBetAmount() {
            return betAmount;
        }

        public void setBetAmount(String betAmount) {
            this.betAmount = betAmount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }


    public static class Response {

        private String betId;//hash betid BetidHashUtils
        private Integer isPayed;
        private String balance;
        private String currency;
        private String errorMessage;

        public String getBetId() {
            return betId;
        }

        public void setBetId(String betId) {
            this.betId = betId;
        }

        public Integer getIsPayed() {
            return isPayed;
        }

        public void setIsPayed(Integer isPayed) {
            this.isPayed = isPayed;
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

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
