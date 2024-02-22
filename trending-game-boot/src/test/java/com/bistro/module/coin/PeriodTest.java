package com.bistro.module.coin;

import com.alibaba.fastjson.JSONObject;
import com.bistro.common.utils.DateUtils;
import com.bistro.module.game.service.ICoinDrawService;
import com.bistro.module.game.service.ICoinPeriodService;
import com.bistro.module.task.CoinDrawTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PeriodTest {

    @Autowired
    ICoinPeriodService coinPeriodService;

    @Autowired
    ICoinDrawService coinDrawService;

    @Resource(name = "coinDrawTask")
    CoinDrawTask coinDrawTask;

    @Test
    public void createPeriodTest() {

        coinPeriodService.createCoinPeriod("BTC", DateUtils.unixTime());
    }

    @Test
    public void addPeriodTest() {
        Map coinPeriodMap = coinPeriodService.getCoinPeriod("BTC");
        List<String> options = new ArrayList<>();
        options.add("GoUp");
        options.add("Small");
        options.add("Digit5");
        options.add("DigitOdd");
        coinPeriodMap.put("drawPrice", new BigDecimal("12.0500").setScale(2, RoundingMode.DOWN).toPlainString());//开奖时的价格
        coinPeriodMap.put("drawResult", JSONObject.toJSONString(options));
        coinPeriodService.addPeriodToSet("BTC", 1L, coinPeriodMap);
    }

    @Test
    public void draw() {
        Map coinPeriodMap = coinPeriodService.getCoinPeriod("BTC");
        coinDrawService.draw("BTC",coinPeriodMap);
    }

    @Test
    public void drawTask() {
        coinDrawTask.draw();
    }
}
