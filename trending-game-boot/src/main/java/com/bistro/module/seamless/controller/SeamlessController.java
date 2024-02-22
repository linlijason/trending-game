package com.bistro.module.seamless.controller;

import com.alibaba.fastjson.JSONObject;
import com.bistro.common.core.domain.model.MySeamlessResult;
import com.bistro.common.exception.seamless.SeamlessException;
import com.bistro.module.seamless.service.ISeamlessService;
import com.bistro.module.seamless.vo.MySeamlessRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@RestController
@RequestMapping("/gameapi/v1")
public class SeamlessController {

    private static final Logger log = LoggerFactory.getLogger(SeamlessController.class);

    @Autowired
    ISeamlessService seamlessService;

    //generate_token
    @PostMapping(value = "/generate_token")
    public MySeamlessResult generateToken(@RequestBody MySeamlessRequest request) {
        try {
            log.info("generateToken request:{}", JSONObject.toJSONString(request));
            MySeamlessResult result = MySeamlessResult.success(seamlessService.generateToken(request));
            log.info("generateToken response:{}", JSONObject.toJSONString(result));
            return result;
        } catch (SeamlessException e) {
            log.error("generateToken request:{}", JSONObject.toJSONString(request), e);
            throw e;
        } catch (Exception e) {
            log.error("generateToken request:{}", JSONObject.toJSONString(request), e);
            throw new SeamlessException(500, "unknown error");
        }

    }

    @PostMapping(value = "/seamless/create_player_account")
    public MySeamlessResult createPlayerAccount(@RequestBody MySeamlessRequest request) {
        try {
            log.info("createPlayerAccount request:{}", JSONObject.toJSONString(request));
            String signKey = seamlessService.verifySignAndAuthToken(request);
            MySeamlessResult result = MySeamlessResult.success(seamlessService.createPlayerAccount(request));
            log.info("createPlayerAccount response:{}", JSONObject.toJSONString(result));
            return result;
        } catch (SeamlessException e) {
            log.error("createPlayerAccount request:{}", JSONObject.toJSONString(request), e);
            throw e;
        } catch (Exception e) {
            log.error("createPlayerAccount request:{}", JSONObject.toJSONString(request), e);
            if (e instanceof DuplicateKeyException) {
                throw new SeamlessException(8, "duplicate username");
            }
            throw new SeamlessException(500, "unknown error");
        }

    }


    @PostMapping(value = "/seamless/query_game_launcher")
    public MySeamlessResult gameLanuncher(@RequestBody MySeamlessRequest request) {
        try {
            log.info("gameLanuncher request:{}", JSONObject.toJSONString(request));
            String signKey = seamlessService.verifySignAndAuthToken(request);
            MySeamlessResult result = MySeamlessResult.success(seamlessService.gameLanuncher(request, signKey));
            log.info("gameLanuncher response:{}", JSONObject.toJSONString(result));
            return result;

        } catch (SeamlessException e) {
            log.error("createPlayerAccount request:{}", JSONObject.toJSONString(request), e);
            throw e;
        } catch (Exception e) {
            log.error("createPlayerAccount request:{}", JSONObject.toJSONString(request), e);
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new SeamlessException(8, "duplicate username");
            }
            throw new SeamlessException(500, "unknown error");
        }

    }

}
