package com.bistro.module.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bistro.common.core.domain.AjaxResult;
import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import com.bistro.constants.Constants;
import com.bistro.module.api.vo.*;
import com.bistro.module.binance.service.IBinanceTickerService;
import com.bistro.module.game.domain.GameRecordInfo;
import com.bistro.module.game.service.*;
import com.bistro.module.seamless.service.ISeamlessService;
import com.bistro.utils.BetidHashUtils;
import com.bistro.web.core.config.CoinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coin")
public class ApiCoinController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ApiCoinController.class);

    @Autowired
    private IGameBaseInfoService gameBaseInfoService;

    @Autowired
    private ISeamlessService seamlessService;

    @Autowired
    private IBinanceTickerService binanceTickerService;

    @Autowired
    private CoinConfig coinConfig;

    @Autowired
    private ICoinBetService coinBetService;

    @Autowired
    private IGameRankService gameRankService;

    @Autowired
    private IUserBalanceService userBalanceService;

    @Autowired
    private ICoinPeriodService coinPeriodService;

    @Autowired
    private IGameRecordInfoService gameRecordInfoService;

    @PostMapping(value = "/gameInfo")
    public AjaxResult getGameInfo(@RequestHeader HttpHeaders headers) {
        getApiUser(headers);
        return AjaxResult.success(gameBaseInfoService.getCoinGameInfo());
    }


    @PostMapping(value = "/userInfo")
    public AjaxResult userInfo(@RequestHeader HttpHeaders headers) {
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(seamlessService.getUserInfo(user.getUid()));
    }

    /**
     * 行情， 第一次查询不带时间，则会返回历史开奖及当期期信息
     *
     * @param headers
     * @param request
     * @return
     */
    @PostMapping(value = "/tickers")
    public AjaxResult tickers(@RequestHeader HttpHeaders headers, @RequestBody TickerRequest request) {
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(binanceTickerService.getTicker(request.getCoin(), request.getStartTime(), request.getRows()));
    }

    @PostMapping(value = "/periodHistory")
    public AjaxResult periodHistory(@RequestHeader HttpHeaders headers, @RequestBody CoinPeriodHistoryRequest request) {
        ApiUser user = getApiUser(headers);
        Integer rows = request.getRows();
        //默认最多取50
        if (rows == null || rows > 50) {
            rows = 50;
        }
        return AjaxResult.success(coinPeriodService.getXPeriod(request.getCoin(), rows));
    }

    @PostMapping(value = "/period")
    public AjaxResult periodAfterDraw(@RequestHeader HttpHeaders headers, @RequestBody CoinPeriodRequest request) {
        log.info("periodAfterDraw:{}", JSONObject.toJSONString(request));
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(coinPeriodService.getPeriod(request.getCoin(), request.getPeriodId(), BetidHashUtils.betId(request.getBetId()), user.getUid()));
    }

    @PostMapping(value = "/nextPeriod")
    public AjaxResult nextPeriod(@RequestHeader HttpHeaders headers, @RequestBody CoinPeriodRequest request) {
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(coinPeriodService.getCoinPeriod(request.getCoin()));
    }

    @PostMapping(value = "/rank")
    public AjaxResult rank(@RequestHeader HttpHeaders headers, @RequestBody CoinRankRequest request) {
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(gameRankService.selectCoinRank(request));
    }

    @PostMapping(value = "/myBet")
    public AjaxResult myBet(@RequestHeader HttpHeaders headers, @RequestBody CoinMyBetRequest request) {
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(coinBetService.selectGameRecordByUidAndCode(user, request));
    }

    @PostMapping(value = "/bet")
    public AjaxResult bet(@RequestHeader HttpHeaders headers, @RequestBody CoinBetRequest request) {
        ApiUser user = getApiUser(headers);
        CoinBetResponse response = null;
        AjaxResult result = null;
        boolean isSuccess = false;
        boolean isOnceLimited = false;
        try {
            response = coinBetService.bet(user, request);
            if (response.getIsPayed() == 0) {
                if (response.getErrorMessage() != null) {
                    result = AjaxResult.error(ApiExceptionMsgEnum.COM_SEAMLESS_ERROR.getCode(), response.getErrorMessage());
                } else {
                    result = AjaxResult.error(ApiExceptionMsgEnum.BET_PAY_ERROR.getCode(), "pay error");
                }
            } else {
                result = AjaxResult.success(response);
                //成功加入排行榜
                gameRankService.addCoinRankBeforeDraw(BetidHashUtils.betId(response.getBetId()), request.getCoin(), request.getPeriodId(),
                        request.getOptions(), new BigDecimal(request.getBetAmount()), response.getUsername());
                //更新余额
                userBalanceService.updateBalance(new BigDecimal(response.getBalance()), user.getUid());
                isSuccess = true;
            }
        } catch (ApiException e) {
            log.error("coin bet:{},{},{}", JSON.toJSONString(user), JSON.toJSONString(request), e.getMessage());
            if (e.getCode().equals(ApiExceptionMsgEnum.BET_ONCE_LIMIT.getCode())) {
                isOnceLimited = true;
            }
            result = AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("coin bet:{},{}", JSON.toJSONString(user), JSON.toJSONString(request), e);
            result = AjaxResult.error(ApiExceptionMsgEnum.COM_UNKNOWN_ERROR.getCode(), "unknown error");
        }
        if (!isSuccess && !isOnceLimited) {
            coinBetService.removeOnceLimitKey(request.getPeriodId(), request.getCoin(), user.getUid());
        }
        return result;
    }


    @PostMapping(value = "/betCancel")
    public AjaxResult betCancel(@RequestHeader HttpHeaders headers, @RequestBody CoinBetCancelRequest request) {
        ApiUser user = getApiUser(headers);
        CoinBetCancelResponse response = null;
        AjaxResult result = null;
        boolean isSuccess = false;
        try {
            response = coinBetService.cancel(user, request);
            if (response.getIsCanceled() == 0) {
                result = AjaxResult.error(ApiExceptionMsgEnum.BET_CANCEL_ERROR.getCode(), "cancel failed");
            } else {
                result = AjaxResult.success(response);
                //从排行榜删除
                Long betId = BetidHashUtils.betId(request.getBetId());
                GameRecordInfo gameRecordInfo = gameRecordInfoService.selectGameRecordInfoByBetId(betId);
                JSONObject jsonObject = JSONObject.parseObject(gameRecordInfo.getGameContent());
                List<CoinBetRequest.BetContent> options = jsonObject.getJSONArray(Constants.COIN_BET_CONTENT).toJavaList(CoinBetRequest.BetContent.class);
                gameRankService.removeCoinRankBeforeDraw(betId, request.getCoin(), request.getPeriodId(),
                        options, new BigDecimal(response.getBetAmount()), response.getUsername());
                //更新余额
                userBalanceService.updateBalance(new BigDecimal(response.getBalance()), user.getUid());
                isSuccess = true;
            }
        } catch (ApiException e) {
            log.error("coin betCancel:{},{},{}", JSON.toJSONString(user), JSON.toJSONString(request), e.getMessage());
            result = AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("coin betCancel:{},{}", JSON.toJSONString(user), JSON.toJSONString(request), e);
            result = AjaxResult.error(ApiExceptionMsgEnum.COM_UNKNOWN_ERROR.getCode(), "unknown error");
        }
        if (isSuccess) {
            coinBetService.removeOnceLimitKey(request.getPeriodId(), request.getCoin(), user.getUid());
        }
        return result;
    }

    @PostMapping(value = "/systemTime")
    public AjaxResult systemTime() {
        return AjaxResult.success(System.currentTimeMillis());
    }

}
