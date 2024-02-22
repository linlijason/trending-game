package com.bistro.module.game.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CoinGuessOption {
    GoUp(1 / 2d),
    GoDown(1 / 2d),
    Big(1 / 2d),
    Small(1 / 2d),
    Digit0(1 / 10d),
    Digit1(1 / 10d),
    Digit2(1 / 10d),
    Digit3(1 / 10d),
    Digit4(1 / 10d),
    Digit5(1 / 10d),
    Digit6(1 / 10d),
    Digit7(1 / 10d),
    Digit8(1 / 10d),
    Digit9(1 / 10d),
    DigitEven(1 / 2d),
    DigitOdd(1 / 2d),
    ;

    private double p;

    CoinGuessOption(double p) {
        this.p = p;
    }

    private static List<String> names;

    static {
        names = Arrays.stream(values()).map(CoinGuessOption::name)
                .collect(Collectors.toList());
    }

    public static List<String> names() {
        return names;
    }

    //原始的赔率，不计抽水
    public BigDecimal originOdds() {
        Double d = 1 / p;
        return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


}
