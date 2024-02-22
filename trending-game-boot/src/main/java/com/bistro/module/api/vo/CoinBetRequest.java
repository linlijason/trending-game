package com.bistro.module.api.vo;

import com.bistro.constants.Constants;
import com.bistro.module.game.domain.CoinGuessOption;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CoinBetRequest {

    private String coin;
    private Long periodId;
    private String betAmount;
    private String currency;
    private List<BetContent> options;

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public String getBetAmount() {
        BigDecimal betAmount = BigDecimal.ZERO;
        for (BetContent bet : options) {
            if (bet.getOption().equals(CoinGuessOption.Small) || bet.getOption().equals(CoinGuessOption.Big)) {
                continue;
            }
            betAmount = betAmount.add(new BigDecimal(bet.getBetAmount()));
        }
        betAmount.setScale(Constants.BET_AMOUNT_DECIMAL_SCALE, RoundingMode.HALF_EVEN);
        return betAmount.stripTrailingZeros().toPlainString();
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

    public List<BetContent> getOptions() {
        return options.stream().sorted(Comparator.comparing(BetContent::getOption)).collect(Collectors.toList());
    }

    public void setOptions(List<BetContent> options) {
        this.options = options;
    }

    public static class BetContent {
        private CoinGuessOption option;
        private String betAmount;
        private String multiplier;
        private String payoutAmount;

        public CoinGuessOption getOption() {
            return option;
        }

        public void setOption(CoinGuessOption option) {
            this.option = option;
        }

        public String getBetAmount() {
            return betAmount;
        }

        public void setBetAmount(String betAmount) {
            this.betAmount = betAmount;
        }

        public String getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(String multiplier) {
            this.multiplier = multiplier;
        }

        public String getPayoutAmount() {
            return payoutAmount;
        }

        public void setPayoutAmount(String payoutAmount) {
            this.payoutAmount = payoutAmount;
        }
    }


}
