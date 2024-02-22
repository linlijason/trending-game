package com.bistro.module.seamless.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.common.exception.seamless.SeamlessException;
import com.bistro.common.utils.DateUtils;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.module.api.vo.PlayerInfo;
import com.bistro.module.merchant.domain.MerchantInfo;
import com.bistro.module.merchant.service.IMerchantInfoService;
import com.bistro.module.player.domain.GameUserInfo;
import com.bistro.module.player.mapper.GameUserInfoMapper;
import com.bistro.module.seamless.client.PlayerInfoRequest;
import com.bistro.module.seamless.client.SeamlessClient;
import com.bistro.module.seamless.client.SeamlessResult;
import com.bistro.module.seamless.service.ISeamlessService;
import com.bistro.module.seamless.vo.MySeamlessRequest;
import com.bistro.utils.SidUtils;
import com.bistro.utils.SignUtils;
import com.bistro.utils.UidHashUtils;
import com.bistro.web.core.config.UrlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SeamlessServiceImpl implements ISeamlessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeamlessServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GameUserInfoMapper gameUserInfoMapper;

    @Autowired
    private SeamlessClient seamlessClient;


    @Autowired
    private UrlConfig urlConfig;

    @Autowired
    private IMerchantInfoService merchantInfoService;


    @Override
    public Map generateToken(MySeamlessRequest request) {
        //验证商户号 秘钥
        MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(request.getMerchantCode());
        if (merchantInfo == null) {
            throw new SeamlessException(2, "invalid merchant_code");
        }
        if (!merchantInfo.getSecureKey().equals(request.getSecureKey())) {
            throw new SeamlessException(3, "invalid secure_key");
        }
        //验签
        verifySign(request, merchantInfo.getSignKey());

        //生成token
        Map<String, String> merchantInfoRedis = new HashMap<>();
        merchantInfoRedis.put("merchantCode", request.getMerchantCode());
        merchantInfoRedis.put("secureKey", request.getSecureKey());
        merchantInfoRedis.put("signKey", merchantInfo.getSignKey());
        String authToken = UUID.randomUUID().toString().replace("-", "");

        String authTokenKey = RedisConstants.AUTH_TOKEN_PREFIX + authToken;
        redisTemplate.opsForHash().putAll(authTokenKey, merchantInfoRedis);
        redisTemplate.expire(authTokenKey, RedisConstants.AUTH_TOKEN_EXPIRE, TimeUnit.SECONDS);
        Map result = new HashMap<>();
        result.put("auth_token", authToken);
        result.put("timeout", RedisConstants.AUTH_TOKEN_EXPIRE);
        return result;
    }

    @Override
    public Map createPlayerAccount(MySeamlessRequest request) {
        String merchantCode = request.getMerchantCode();
        String username = request.getUsername();
        GameUserInfo gameUserInfo = new GameUserInfo();
        gameUserInfo.setFromCode(merchantCode);
        gameUserInfo.setName(username);
        gameUserInfo.setOpenId(UUID.randomUUID().toString().replace("-", ""));
        gameUserInfo.setCreateTime(DateUtils.getNowDate());
        gameUserInfo.setUpdateTime(DateUtils.getNowDate());
        gameUserInfoMapper.insertGameUserInfo(gameUserInfo);
        Map result = new HashMap();
        result.put("username", request.getUsername());
        return result;
    }

    @Override
    public String verifySignAndAuthToken(MySeamlessRequest request) {

        if (request.getAuthToken() == null) {
            throw new SeamlessException(4, "invalid auth_token");
        }
        Map result = redisTemplate.opsForHash().entries(RedisConstants.AUTH_TOKEN_PREFIX + request.getAuthToken());
        if (result == null || result.isEmpty()) {
            throw new SeamlessException(4, "invalid auth_token");
        }

        if (!request.getMerchantCode().equals(result.get("merchantCode"))) {
            throw new SeamlessException(2, "invalid merchant_code");
        }

        String signKey = result.get("signKey").toString();
        verifySign(request, signKey);
        return signKey;

    }

    private void verifySign(MySeamlessRequest request, String signKey) {

        //获取签名key
        boolean verify = false;
        if (request.getSign() != null) {
            verify = request.getSign().equals(SignUtils.sha1Sign(SignUtils.concatSignString(request, signKey)));
        }
        if (!verify) {
            throw new SeamlessException(1, "invalid signature");
        }

    }

    @Override
    public Map gameLanuncher(MySeamlessRequest request, String signKey) {
        PlayerInfoRequest playerInfoRequest = new PlayerInfoRequest();
        playerInfoRequest.setToken(request.getToken());
        playerInfoRequest.setTimestamp(System.currentTimeMillis() / 1000);
        playerInfoRequest.setUniqueId(UUID.randomUUID().toString().replace("-", ""));
        playerInfoRequest.setMerchantCode(request.getMerchantCode());
        String sign = SignUtils.sha1Sign(SignUtils.concatSignString(playerInfoRequest, signKey));
        playerInfoRequest.setSign(sign);
        //通过token查询用户信息
        LOGGER.info("seamlessClient.playerInfo request: {}", JSONObject.toJSONString(playerInfoRequest));
        SeamlessResult seamlessResult = seamlessClient.playerInfo(playerInfoRequest);
//        SeamlessResult seamlessResult = null;//todo
        LOGGER.info("seamlessClient.playerInfo response: {}", JSONObject.toJSONString(seamlessResult));

        String userName = seamlessResult.getUsername(); //"devrctesting";
        Long uid = null;
        //查询我们这边用户是否存在 不存在插入数据
        GameUserInfo gameUserInfo = gameUserInfoMapper.findByUserNameAndFromCode(userName, request.getMerchantCode());
        if (gameUserInfo == null) {
//            if (!seamlessResult.isSuccess()) {
            GameUserInfo insertUser = new GameUserInfo();
            insertUser.setFromCode(request.getMerchantCode());
            insertUser.setName(userName);
            insertUser.setCreateTime(DateUtils.getNowDate());
            insertUser.setUpdateTime(insertUser.getCreateTime());
            insertUser.setOpenId(UUID.randomUUID().toString().replace("-", ""));
            gameUserInfoMapper.insertGameUserInfo(insertUser);
            uid = insertUser.getId();
//            }
        } else {
            uid = gameUserInfo.getId();
        }

        //缓存token 后续调用平台接口要使用token
        redisTemplate.opsForValue().set(RedisConstants.SEAMLESS_TOKEN_PREFIX + uid, request.getToken(), 2, TimeUnit.HOURS);

        //加密uid
        //后边加上game_code每个游戏 webscoket id参数不一样
        String hashUId = UidHashUtils.hashId(uid) + request.getGameCode();
        String encodeHashUid = SidUtils.encode(hashUId);

        Map map = new HashMap();
        map.put("game_url", urlConfig.getGameLaunchUrl().get(request.getGameCode()) + "?id=" + hashUId + "&sid=" + encodeHashUid);
        return map;
    }

    @Override
    public String getSeamleesToken(Long uid) {
        String token = redisTemplate.opsForValue().get(RedisConstants.SEAMLESS_TOKEN_PREFIX + uid);
        if (token == null) {
            throw new ApiException(ApiExceptionMsgEnum.SEAMLESS_TOKEN_EXPIRED_ERROR.getCode(), "token is expired, please relaunch game");
        } else {
            return token;
        }

    }

    @Override
    public PlayerInfo getUserInfo(Long uid) {
        GameUserInfo userInfo = gameUserInfoMapper.selectGameUserInfoById(uid);
        PlayerInfoRequest playerInfoRequest = new PlayerInfoRequest();
        playerInfoRequest.setToken(getSeamleesToken(uid));
//        playerInfoRequest.setToken("N2lXVi80ZW5IL3FaM0l5b1RLc1FwUT09");
        playerInfoRequest.setTimestamp(System.currentTimeMillis() / 1000);
        playerInfoRequest.setUniqueId(UUID.randomUUID().toString().replace("-", ""));
        playerInfoRequest.setMerchantCode(userInfo.getFromCode());
        MerchantInfo merchantInfo = merchantInfoService.selectMerchantInfoByCode(userInfo.getFromCode());
        String sign = SignUtils.sha1Sign(SignUtils.concatSignString(playerInfoRequest, merchantInfo.getSignKey()));
        playerInfoRequest.setSign(sign);
        LOGGER.info("seamlessClient.playerInfo request:{}", JSONObject.toJSONString(playerInfoRequest));
        SeamlessResult seamlessResult = null;
        try {
            seamlessResult = seamlessClient.playerInfo(playerInfoRequest);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ApiExceptionMsgEnum.USER_INFO_ERROR.getCode(), "get user info failed");
        }
        LOGGER.info("seamlessClient.playerInfo response:{}", JSONObject.toJSONString(seamlessResult));
//        SeamlessResult seamlessResult = mockUserSeamlessResult(userInfo);
        PlayerInfo playerInfo = new PlayerInfo();
        if (seamlessResult.isSuccess()) {
//            playerInfo.setUid(uid);
            playerInfo.setBalance(seamlessResult.getBalance());
            playerInfo.setCurrency(seamlessResult.getCurrency());
            playerInfo.setUsername(seamlessResult.getUsername());
        } else {
            throw new ApiException(ApiExceptionMsgEnum.USER_INFO_ERROR.getCode(), "get user info failed");
        }
        return playerInfo;
    }

    private SeamlessResult mockUserSeamlessResult(GameUserInfo userInfo) {
        SeamlessResult mock = new SeamlessResult();
        mock.setSuccess(true);
        mock.setBalance(Constants.mockAmount);
        mock.setCurrency("BRL");
        mock.setUsername(userInfo.getName());
        return mock;

    }
}
