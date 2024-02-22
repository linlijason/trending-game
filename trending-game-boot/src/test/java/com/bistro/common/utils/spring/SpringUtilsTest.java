package com.bistro.common.utils.spring;

import com.bistro.module.game.domain.CoinGuessOptionConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SpringUtilsTest {

    @Test
    public void testToObject() throws Exception{
        ObjectMapper objectMapper=new ObjectMapper();

        List<CoinGuessOptionConfig> list = objectMapper.readValue("[{\"option\":\"GoUp\"}]", new TypeReference<List<CoinGuessOptionConfig>>() {
        });
        System.out.println(list);
    }
}