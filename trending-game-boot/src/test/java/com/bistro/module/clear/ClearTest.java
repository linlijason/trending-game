package com.bistro.module.clear;

import com.bistro.module.task.CleanDataTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ClearTest {

    @Resource(name= "cleanDataTask")
    private CleanDataTask cleanDataTask;

    @Test
    public void clearMinesRank(){
        cleanDataTask.cleanMinesRankData(1);
    }
}
