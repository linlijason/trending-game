package com.bistro.common.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
public class CleanRankRedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;



    @Test
    public void ttt() throws Exception{
//        redisTemplate.delete(Arrays.asList(RedisConstants.RANK_ALL, RedisConstants.RANK_PAY_OUT, RedisConstants.RANK_MULTIPLIER));

    }

}