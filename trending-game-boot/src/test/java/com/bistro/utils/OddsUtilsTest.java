package com.bistro.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class OddsUtilsTest {

    @Test
    public void calcOdds() {
        System.out.println(OddsUtils.calcOdds(
                BigDecimal.valueOf(25/24d),BigDecimal.valueOf(0.05)));
    }
}