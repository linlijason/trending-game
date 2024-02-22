package com.bistro.module.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.utils.DateUtils;
import com.bistro.constants.Constants;
import com.bistro.message.model.MessageUser;
import com.bistro.module.game.domain.GameBet;
import com.bistro.module.game.domain.GameEnum;
import com.bistro.module.game.domain.PayoutInfo;
import com.bistro.module.merchant.service.IMerchantInfoService;
import com.bistro.module.payment.domain.GamePaymentInfo;
import com.bistro.module.payment.domain.GamePaymentTimeout;
import com.bistro.module.payment.mapper.GamePaymentInfoMapper;
import com.bistro.module.payment.service.IGamePaymentTimeoutService;
import com.bistro.module.payment.service.IPaymentService;
import com.bistro.module.seamless.client.*;
import com.bistro.utils.BetidHashUtils;
import com.bistro.utils.SignUtils;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.util.*;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final Logger timeout_logger = LoggerFactory.getLogger("timeout");

    @Autowired
    private SeamlessClient seamlessClient;

    @Autowired
    private IMerchantInfoService merchantInfoService;

    @Autowired
    private GamePaymentInfoMapper gamePaymentInfoMapper;

    @Autowired
    private IGamePaymentTimeoutService gamePaymentTimeoutService;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public SeamlessResult bet(GameBet gameBet, MessageUser user) {

        GamePaymentInfo gamePaymentInfo = new GamePaymentInfo();
        gamePaymentInfo.setAmount(gameBet.getBetAmount());
        gamePaymentInfo.setPaymentChannel(user.getMerchantCode());
        gamePaymentInfo.setPaymentOrderNo(UUID.randomUUID().toString().replace("-", ""));
        gamePaymentInfo.setChanelOrderNo(BetidHashUtils.hashId(gameBet.getId()) + "_" + GameEnum.PaymentBusinessTypeEnum.BET.getCode());
        gamePaymentInfo.setOpenId(user.getOpenId());
        gamePaymentInfo.setBusinessId(gameBet.getId());
        gamePaymentInfo.setBusinsesType(GameEnum.PaymentBusinessTypeEnum.BET.getCode());
        gamePaymentInfo.setStatus(1);
        gamePaymentInfo.setCurrency(gameBet.getCurrency());
        gamePaymentInfo.setUid(Long.valueOf(user.getUid()));
        gamePaymentInfo.setCreateTime(DateUtils.getNowDate());
        gamePaymentInfo.setUpdateTime(gamePaymentInfo.getCreateTime());

        BetRequest request = new BetRequest();
        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setUniqueId(gamePaymentInfo.getChanelOrderNo());
        request.setBetId(gameBet.getId());
        request.setGameCode(gameBet.getGameCode());
        request.setAmount(gameBet.getBetAmount().setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
        request.setNumber("");
        request.setCurrency(gameBet.getCurrency());
        request.setMerchantCode(user.getMerchantCode());
        request.setRoundId("");
        request.setUsername(user.getUsername());

        request.setSign(SignUtils.sha1Sign(SignUtils.concatSignString(request, signKey(request.getMerchantCode(), user.getSignKey()))));
        String requestStr = JSONObject.toJSONString(request);
        gamePaymentInfo.setContent("{\"request\":" + requestStr + "}");
        gamePaymentInfoMapper.insertGamePaymentInfo(gamePaymentInfo);

        LOGGER.info("seamlessClient.bet request: {}", requestStr);
        SeamlessResult seamlessResult = null;
        try {
            seamlessResult = seamlessClient.bet(request);
        } catch (ApiException e) {
            seamlessResult = new SeamlessResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            seamlessResult.setOriginalErrMsg(e.getMessage());
        } catch (Exception e) {
            if (e instanceof RetryableException && e.getCause() instanceof SocketTimeoutException) {
                timeout_logger.info("bet timeout:{},{}", e.getCause().getMessage(), requestStr);
                timeout(gamePaymentInfo);
            }
            seamlessResult = new SeamlessResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            LOGGER.error("seamlessClient.bet error:{}", JSONObject.toJSONString(request), e);
        }
        String responseStr = JSONObject.toJSONString(seamlessResult);
        LOGGER.info("seamlessClient.bet response: {}", responseStr);
        gamePaymentInfo.setContent("{\"request\":" + requestStr + ",\"response\":" + responseStr + "}");
        try {
            if (seamlessResult.isSuccess()) {
                gamePaymentInfo.setStatus(2);
                gamePaymentInfo.setUpdateTime(DateUtils.getNowDate());
                gamePaymentInfoMapper.updateGamePaymentInfo(gamePaymentInfo);
            } else {
                gamePaymentInfo.setStatus(3);
                gamePaymentInfo.setUpdateTime(DateUtils.getNowDate());
                gamePaymentInfoMapper.updateGamePaymentInfo(gamePaymentInfo);
            }
        } catch (Exception e) {
            LOGGER.error("update game playment info error:{}", e);
        }
        return seamlessResult;

    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public SeamlessResult payout(GameBet gameBet, MessageUser user) {

        GamePaymentInfo gamePaymentInfo = new GamePaymentInfo();
        gamePaymentInfo.setUid(Long.valueOf(user.getUid()));
        gamePaymentInfo.setAmount(gameBet.getPayoutAmount().setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_DOWN));
        gamePaymentInfo.setPaymentChannel(user.getMerchantCode());
        gamePaymentInfo.setPaymentOrderNo(UUID.randomUUID().toString().replace("-", ""));
        gamePaymentInfo.setChanelOrderNo(BetidHashUtils.hashId(gameBet.getId()) + "_" + GameEnum.PaymentBusinessTypeEnum.WD.getCode());
        gamePaymentInfo.setOpenId(user.getOpenId());
        gamePaymentInfo.setBusinessId(gameBet.getId());
        gamePaymentInfo.setBusinsesType(GameEnum.PaymentBusinessTypeEnum.WD.getCode());
        gamePaymentInfo.setStatus(1);
        gamePaymentInfo.setCurrency(gameBet.getCurrency());
        gamePaymentInfo.setCreateTime(DateUtils.getNowDate());
        gamePaymentInfo.setUpdateTime(gamePaymentInfo.getCreateTime());

        PayoutRequest request = new PayoutRequest();
        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setUniqueId(gamePaymentInfo.getChanelOrderNo());
        request.setBetId(gameBet.getId());
        request.setGameCode(gameBet.getGameCode());
        request.setAmount(gameBet.getPayoutAmount().setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
        request.setNumber("");
        request.setCurrency(gameBet.getCurrency());
        request.setMerchantCode(user.getMerchantCode());
        request.setRoundId("");
        request.setUsername(user.getUsername());

        request.setSign(SignUtils.sha1Sign(SignUtils.concatSignString(request, signKey(request.getMerchantCode(), user.getSignKey()))));
        String requestStr = JSONObject.toJSONString(request);
        gamePaymentInfo.setContent("{\"request\":" + requestStr + "}");
        gamePaymentInfoMapper.insertGamePaymentInfo(gamePaymentInfo);

        LOGGER.info("seamlessClient.payout request: {}", requestStr);
        SeamlessResult seamlessResult = null;
        try {
            seamlessResult = seamlessClient.payout(request);
        } catch (ApiException e) {
            seamlessResult = new SeamlessResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            seamlessResult.setOriginalErrMsg(e.getMessage());
        } catch (Exception e) {
            if (e instanceof RetryableException && e.getCause() instanceof SocketTimeoutException) {
                timeout_logger.info("payout timeout:{},{}", e.getCause().getMessage(), requestStr);
                timeout(gamePaymentInfo);
            }
            seamlessResult = new SeamlessResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            LOGGER.error("seamlessClient.payout error:{}", JSONObject.toJSONString(request), e);
        }
        String responseStr = JSONObject.toJSONString(seamlessResult);
        LOGGER.info("seamlessClient.payout response: {}", responseStr);
        gamePaymentInfo.setContent("{\"request\":" + requestStr + ",\"response\":" + responseStr + "}");
        try {
            if (seamlessResult.isSuccess()) {
                gamePaymentInfo.setStatus(2);
                gamePaymentInfo.setUpdateTime(DateUtils.getNowDate());
                gamePaymentInfoMapper.updateGamePaymentInfo(gamePaymentInfo);
            } else {
                gamePaymentInfo.setStatus(3);
                gamePaymentInfo.setUpdateTime(DateUtils.getNowDate());
                gamePaymentInfoMapper.updateGamePaymentInfo(gamePaymentInfo);
            }
        } catch (Exception e) {
            LOGGER.error("update game playment info error:{}", e);
        }
        return seamlessResult;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public SeamlessResult refund(GameBet gameBet, MessageUser user, GameEnum.PaymentBusinessTypeEnum paymentType) {

        GamePaymentInfo gamePaymentInfo = new GamePaymentInfo();
        gamePaymentInfo.setAmount(gameBet.getBetAmount().setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_DOWN));
        gamePaymentInfo.setPaymentChannel(user.getMerchantCode());
        gamePaymentInfo.setPaymentOrderNo(UUID.randomUUID().toString().replace("-", ""));
        gamePaymentInfo.setChanelOrderNo(BetidHashUtils.hashId(gameBet.getId()) + "_" + GameEnum.PaymentBusinessTypeEnum.REFUND.getCode());
        gamePaymentInfo.setOpenId(user.getOpenId());
        gamePaymentInfo.setBusinessId(gameBet.getId());
        gamePaymentInfo.setBusinsesType(paymentType.getCode());
        gamePaymentInfo.setStatus(1);
        gamePaymentInfo.setCurrency(gameBet.getCurrency());
        gamePaymentInfo.setUid(Long.valueOf(user.getUid()));
        gamePaymentInfo.setCreateTime(DateUtils.getNowDate());
        gamePaymentInfo.setUpdateTime(gamePaymentInfo.getCreateTime());

        RefundRequest request = new RefundRequest();
        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setUniqueId(gamePaymentInfo.getChanelOrderNo());
        request.setBetId(gameBet.getId());
        request.setGameCode(gameBet.getGameCode());
        request.setAmount(gameBet.getBetAmount().setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
        request.setType(1);
        request.setCurrency(gameBet.getCurrency());
        request.setMerchantCode(user.getMerchantCode());
        request.setRoundId("");
        request.setUsername(user.getUsername());

        request.setSign(SignUtils.sha1Sign(SignUtils.concatSignString(request, signKey(request.getMerchantCode(), user.getSignKey()))));
        String requestStr = JSONObject.toJSONString(request);
        gamePaymentInfo.setContent("{\"request\":" + requestStr + "}");
        gamePaymentInfoMapper.insertGamePaymentInfo(gamePaymentInfo);

        LOGGER.info("seamlessClient.refund request: {}", requestStr);
        SeamlessResult seamlessResult = null;
        try {
            seamlessResult = seamlessClient.refund(request);
        } catch (ApiException e) {
            seamlessResult = new SeamlessResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            seamlessResult.setOriginalErrMsg(e.getMessage());
        } catch (Exception e) {
            if (e instanceof RetryableException && e.getCause() instanceof SocketTimeoutException) {
                timeout_logger.info("refund timeout:{},{}", e.getCause().getMessage(), requestStr);
            }
            seamlessResult = new SeamlessResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            LOGGER.error("seamlessClient.refund error:{}", JSONObject.toJSONString(request), e);
        }
        String responseStr = JSONObject.toJSONString(seamlessResult);
        LOGGER.info("seamlessClient.refund response: {}", responseStr);
        gamePaymentInfo.setContent("{\"request\":" + requestStr + ",\"response\":" + responseStr + "}");
        try {
            if (seamlessResult.isSuccess()) {
                gamePaymentInfo.setStatus(2);
                gamePaymentInfo.setUpdateTime(DateUtils.getNowDate());
                gamePaymentInfoMapper.updateGamePaymentInfo(gamePaymentInfo);
            } else {
                gamePaymentInfo.setStatus(3);
                gamePaymentInfo.setUpdateTime(DateUtils.getNowDate());
                gamePaymentInfoMapper.updateGamePaymentInfo(gamePaymentInfo);
            }
        } catch (Exception e) {
            LOGGER.error("update game playment info error:{}", e);
        }

        return seamlessResult;

    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public SeamlessBatchPayoutResult batchPayout(List<PayoutInfo> payoutInfoList) {
        if (payoutInfoList == null || payoutInfoList.size() == 0) {
            return null;
        }

        //多个payout用同一个paymentOrderNo
        String paymentOrderNo = UUID.randomUUID().toString().replace("-", "") + "_";
        int paymentOrderNoIndex = 0;

        List<GamePaymentInfo> gamePaymentInfoList = new ArrayList<>();
        List<BatchPayoutRequest.Payout> payoutList = new ArrayList<>();
        Map<String, PayoutInfo> payoutInfoMap = new HashMap<>();
        for (PayoutInfo payoutInfo : payoutInfoList) {
            GameBet gameBet = payoutInfo.getBet();
            MessageUser user = payoutInfo.getUser();

            GamePaymentInfo gamePaymentInfo = new GamePaymentInfo();
            gamePaymentInfo.setUid(Long.valueOf(user.getUid()));
            gamePaymentInfo.setAmount(gameBet.getPayoutAmount().setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_DOWN));
            gamePaymentInfo.setPaymentChannel(user.getMerchantCode());
            gamePaymentInfo.setPaymentOrderNo(paymentOrderNo + (++paymentOrderNoIndex));
            gamePaymentInfo.setChanelOrderNo(BetidHashUtils.hashId(gameBet.getId()) + "_" + GameEnum.PaymentBusinessTypeEnum.WD.getCode());
            gamePaymentInfo.setOpenId(user.getOpenId());
            gamePaymentInfo.setBusinessId(gameBet.getId());
            gamePaymentInfo.setBusinsesType(GameEnum.PaymentBusinessTypeEnum.WD.getCode());
            gamePaymentInfo.setStatus(1);
            gamePaymentInfo.setCurrency(gameBet.getCurrency());
            gamePaymentInfo.setCreateTime(DateUtils.getNowDate());
            gamePaymentInfo.setUpdateTime(gamePaymentInfo.getCreateTime());


            BatchPayoutRequest.Payout payout = new BatchPayoutRequest.Payout();
            payout.setUniqueId(gamePaymentInfo.getChanelOrderNo());
            payout.setBetId(gameBet.getId());
            payout.setGameCode(gameBet.getGameCode());
            payout.setAmount(gameBet.getPayoutAmount().setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
            payout.setNumber("");
            payout.setCurrency(gameBet.getCurrency());
            payout.setRoundId("");
            payout.setUsername(user.getUsername());
            payoutList.add(payout);
            payoutInfoMap.put(payout.getUniqueId(), payoutInfo);
            String requestStr = JSONObject.toJSONString(payout);
            gamePaymentInfo.setContent("{\"request\":" + requestStr + "}");
            gamePaymentInfoList.add(gamePaymentInfo);
        }

        gamePaymentInfoMapper.batchInsertGamePaymentInfo(gamePaymentInfoList);
        BatchPayoutRequest request = new BatchPayoutRequest();
        request.setTimestamp(DateUtils.unixTime());
        request.setMerchantCode(payoutInfoList.get(0).getUser().getMerchantCode());
        request.setPayouts(payoutList);
        request.setSign(SignUtils.sha1Sign(SignUtils.concatSignString(request, merchantInfoService.selectMerchantInfoByCode(request.getMerchantCode()).getSignKey())));

        String requestStr = JSONObject.toJSONString(request);
        SeamlessBatchPayoutResult seamlessResult = null;
        try {
            LOGGER.info("seamlessClient.batchPayout request: {}", requestStr);
            seamlessResult = seamlessClient.batchPayout(request);
            LOGGER.info("seamlessClient.batchPayout response: {}", JSONObject.toJSONString(seamlessResult));
        } catch (ApiException e) {
            seamlessResult = new SeamlessBatchPayoutResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            seamlessResult.setOriginalErrMsg(e.getMessage());
        } catch (Exception e) {
            if (e instanceof RetryableException && e.getCause() instanceof SocketTimeoutException) {
                timeout_logger.info("batchPayout timeout:{},{}", e.getCause().getMessage(), requestStr);
                timeout(gamePaymentInfoList);
            }
            seamlessResult = new SeamlessBatchPayoutResult();
            seamlessResult.setSuccess(false);
            seamlessResult.setErrMsg(e.getMessage());
            LOGGER.error("seamlessClient.batchPayout error:{}", JSONObject.toJSONString(request), e);
        }
        seamlessResult.setPayoutInfos(payoutInfoMap);
        Date now = DateUtils.getNowDate();
        try {
            if (seamlessResult.isSuccess()) {
                List<SeamlessBatchPayoutResult.Payout> payouts = seamlessResult.getPayouts();
                for (SeamlessBatchPayoutResult.Payout payout : payouts) {
                    for (GamePaymentInfo gamePaymentInfo : gamePaymentInfoList) {
                        if (payout.getUniqueId().equals(gamePaymentInfo.getPaymentOrderNo())) {
                            JSONObject resultJson = JSONObject.parseObject(gamePaymentInfo.getContent());
                            resultJson.put("response", payout);
                            gamePaymentInfo.setContent(resultJson.toJSONString());
                            if (payout.isSuccess()) {
                                gamePaymentInfo.setStatus(2);
                                gamePaymentInfo.setUpdateTime(now);
                            } else {
                                gamePaymentInfo.setStatus(3);
                                gamePaymentInfo.setUpdateTime(now);
                            }
                            break;
                        }
                    }
                }
            } else {
                for (GamePaymentInfo gamePaymentInfo : gamePaymentInfoList) {
                    JSONObject resultJson = JSONObject.parseObject(gamePaymentInfo.getContent());
                    resultJson.put("response", seamlessResult);
                    gamePaymentInfo.setContent(resultJson.toJSONString());
                    gamePaymentInfo.setStatus(3);
                    gamePaymentInfo.setUpdateTime(now);
                }
            }

            //update gamePaymentInfo
            gamePaymentInfoMapper.updateBatchResult(gamePaymentInfoList);
        } catch (Exception e) {
            LOGGER.error("update game playment info error:{}", e);
        }
        return seamlessResult;
    }

    private String signKey(String merchantCode, String signKey) {
        if (signKey == null) {
            return merchantInfoService.selectMerchantInfoByCode(merchantCode).getSignKey();
        } else {
            return signKey;
        }
    }

    private void timeout(List<GamePaymentInfo> gamePaymentInfoList) {
        try {
            List<GamePaymentTimeout> timeoutList = new ArrayList<>();
            for (GamePaymentInfo gamePaymentInfo : gamePaymentInfoList) {
                GamePaymentTimeout gamePaymentTimeout = new GamePaymentTimeout();
                gamePaymentTimeout.setBetId(gamePaymentInfo.getBusinessId());
                gamePaymentTimeout.setMerchantCode(gamePaymentInfo.getPaymentChannel());
                gamePaymentTimeout.setPaymentType(gamePaymentInfo.getBusinsesType());
                timeoutList.add(gamePaymentTimeout);
            }
            gamePaymentTimeoutService.batchInsert(timeoutList);
        } catch (Exception e) {
            LOGGER.error("timeout batchInsert error:", e);

        }
    }

    private void timeout(GamePaymentInfo gamePaymentInfo) {
        try {
            GamePaymentTimeout gamePaymentTimeout = new GamePaymentTimeout();
            gamePaymentTimeout.setBetId(gamePaymentInfo.getBusinessId());
            gamePaymentTimeout.setMerchantCode(gamePaymentInfo.getPaymentChannel());
            gamePaymentTimeout.setPaymentType(gamePaymentInfo.getBusinsesType());
            gamePaymentTimeoutService.insertGamePaymentTimeout(gamePaymentTimeout);
        } catch (Exception e) {
            LOGGER.error("timeout insert error:", e);
        }
    }
}
