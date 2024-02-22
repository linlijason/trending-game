package com.bistro.utils;

import java.math.BigDecimal;

public class OddsUtils {

    //根据抽水，和原始赔率，计算赔率
    public static BigDecimal calcOdds(BigDecimal originOdds, BigDecimal comm) {

        // (原-1) * (1-抽)+1
        BigDecimal odds = originOdds.subtract(BigDecimal.ONE)
                .multiply(BigDecimal.ONE.subtract(comm))
                .add(BigDecimal.ONE);

        return odds.setScale(2, BigDecimal.ROUND_HALF_UP);


    }
}
