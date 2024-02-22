package com.bistro.module.game.domain;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class CoinGuessOptionTest {

    @Test
    public void testGetOdds() {
        Map map=new HashMap();
        for (CoinGuessOption value : CoinGuessOption.values()) {
            map.put(value.name(),"");
        }
        System.out.println(JSON.toJSONString(map));
    }

}