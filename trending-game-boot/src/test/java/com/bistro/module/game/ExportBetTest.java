package com.bistro.module.game;

import com.bistro.common.core.domain.entity.SysUser;
import com.bistro.common.core.domain.model.LoginUser;
import com.bistro.common.utils.DateUtils;
import com.bistro.module.game.domain.BetGameUserQuery;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.service.BetGameUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
public class ExportBetTest {

    @Autowired
    private BetGameUserService betGameUserService;

    @Autowired
    private GameBetMapper gameBetMapper;

    @Test
    public void testExport() {
        BetGameUserQuery query = new BetGameUserQuery();
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUserName("aaa");
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        loginUser.setUser(sysUser);
        betGameUserService.exportReport(query, loginUser);
    }

    @Test
    public void testInsert() {

        Date now = DateUtils.getNowDate();
        for (int i = 0; i < 300000; i++) {
            GameBet gameBet = new GameBet();
            gameBet.setBetAmount(new BigDecimal(new Random().nextDouble()).setScale(2, RoundingMode.DOWN));
            gameBet.setMultiplier(new BigDecimal(new Random().nextDouble()).setScale(2, RoundingMode.DOWN));
            gameBet.setUid(1L);
            gameBet.setUserName("batchInsert" + i);
            gameBet.setPayoutAmount(new BigDecimal(new Random().nextDouble()).setScale(2, RoundingMode.DOWN));
            gameBet.setCreateTime(now);
            gameBet.setUpdateTime(now);
            gameBet.setStatus(new Random().nextInt(8));
            gameBet.setCurrency("BRL");
            gameBet.setGameCode("60");
            gameBet.setOpenId("a");
            gameBetMapper.insertGameBet(gameBet);
        }

    }
}
