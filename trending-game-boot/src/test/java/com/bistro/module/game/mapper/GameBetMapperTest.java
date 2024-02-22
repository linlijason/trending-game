package com.bistro.module.game.mapper;

import com.bistro.module.api.vo.GamePlayRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
public class GameBetMapperTest {

    @Autowired
    private GameBetMapper mapper;

    @Test
    public void test()
    {
        GamePlayRequest request= new GamePlayRequest();
        request.setGameCode("mines");
        request.setRows(10);
        request.setUid(1L);
        request.setOrder("create_time");
        mapper.selectBetHistory(request);
    }

}