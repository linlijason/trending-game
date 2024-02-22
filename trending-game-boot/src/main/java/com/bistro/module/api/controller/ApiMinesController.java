package com.bistro.module.api.controller;

import com.bistro.common.core.domain.AjaxResult;
import com.bistro.constants.Constants;
import com.bistro.module.api.vo.ApiUser;
import com.bistro.module.api.vo.GamePlayInfo;
import com.bistro.module.api.vo.GamePlayRequest;
import com.bistro.module.game.service.IGameBaseInfoService;
import com.bistro.module.game.service.IGamePlayService;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.module.seamless.service.ISeamlessService;
import com.bistro.utils.BetidHashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mines")
public class ApiMinesController extends BaseController {

    @Autowired
    private IGameBaseInfoService gameBaseInfoService;

    @Autowired
    private IGamePlayService gamePlayService;

    @Autowired
    private IGameRankService gameRankService;

    @Autowired
    private ISeamlessService seamlessService;

    @PostMapping(value = "/gameInfo")
    public AjaxResult getGameInfo(@RequestHeader HttpHeaders headers) {
        getApiUser(headers);
        return AjaxResult.success(gameBaseInfoService.getGameInfo(Constants.MINES_GAME_CODE));
    }

    @PostMapping(value = "/ongoingGame/{gameCode}")
    public AjaxResult getOngoingGame(@RequestHeader HttpHeaders headers, @PathVariable String gameCode) {
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(gamePlayService.getOngoingGame(user.getUid(), gameCode));
    }

//    @PostMapping(value = "/myBetHistory/{gameCode}")
//    public AjaxResult getMyBetHistory(@RequestHeader HttpHeaders headers, @RequestBody GamePlayRequest request, , @PathVariable String gameCode) {
//        ApiUser user = getApiUser(headers);
//        request.setUid(user.getUid());
//        if (request.getRows() == null) {
//            request.setRows(10);
//        }
//        if (request.getRows() > 50) {//最多查50条
//            request.setRows(50);
//        }
//        Map result = new HashMap<>();
//
//        List<GamePlayInfo> list = gameRankService.selectBetHistoryByUid(request);
//        for (GamePlayInfo item : list) {
//            item.setBetId(BetidHashUtils.hashId(item.getId()));
//            item.setId(null);//不对外展示
//        }
//        result.put("list", list);
//        return AjaxResult.success(result);
//    }

    @PostMapping(value = "/betDetail/{hashBetId}")
    public AjaxResult getBetDetail(@RequestHeader HttpHeaders headers, @PathVariable String hashBetId) {
        getApiUser(headers);
        return AjaxResult.success(gameRankService.getGameBetDetailByHashBetId(hashBetId));
    }

    @PostMapping(value = "/rank/{gameCode}")
    public AjaxResult rankList(@RequestHeader HttpHeaders headers, @RequestBody GamePlayRequest request, @PathVariable String gameCode) {
        ApiUser user = getApiUser(headers);
        request.setUid(user.getUid());
        request.setGameCode(gameCode);
        if (request.getRows() == null) {
            request.setRows(10);
        }
        if (request.getRows() > 50) {//最多查50条
            request.setRows(50);
        }
        //1 -按payout 从高到第， 2-按multiplier从高到低  3- 我的游戏记录 4- 所有游戏记录

        //都先用uid的数据返回 后边再搞假数据
        List<GamePlayInfo> list = null;
        if (request.getType() == 3) {
            request.setOrder("create_time");
            list = gameRankService.selectBetHistory(request);
        } else if (request.getType() == 4) {
//            request.setOrder("create_time");
//            request.setUid(null);
            list = gameRankService.selectRank(request);
        } else if (request.getType() == 1) {
//            request.setUid(null);
//            request.setOrder("payout_amount");
            list = gameRankService.selectRank(request);
        } else if (request.getType() == 2) {
//            request.setUid(null);
//            request.setOrder("multiplier");
            list = gameRankService.selectRank(request);
        }

        String queryStart = request.getQueryStart();
        if (list != null && list.size() > 0) {
            queryStart = list.get(0).getQueryStart();//取第一条数据
            for (GamePlayInfo item : list) {
                if (item.getId() != null) {
                    item.setBetId(BetidHashUtils.hashId(item.getId()));
                    item.setMultiplier(item.getMultiplier().setScale(2, RoundingMode.DOWN));
                    item.setBetAmount(item.getBetAmount().setScale(2, RoundingMode.DOWN));
                    item.setPayoutAmount(item.getPayoutAmount().setScale(2, RoundingMode.DOWN));
                }
                item.setPlayer(new StringBuilder(item.getPlayer()).replace(1, item.getPlayer().length() - 1, "****").toString());
                item.setQueryStart(null);
                item.setId(null);//不对外展示
            }
        }

        Map result = new HashMap<>();
        result.put("queryStart", queryStart);
        result.put("list", list);
        return AjaxResult.success(result);
    }

    @PostMapping(value = "/userInfo")
    public AjaxResult userInfo(@RequestHeader HttpHeaders headers) {
        ApiUser user = getApiUser(headers);
        return AjaxResult.success(seamlessService.getUserInfo(user.getUid()));
    }
}
