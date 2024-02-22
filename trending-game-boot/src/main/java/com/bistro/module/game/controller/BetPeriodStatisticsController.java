package com.bistro.module.game.controller;

import com.bistro.common.core.controller.BaseController;
import com.bistro.module.game.domain.BetPeriodStatisticsQuery;
import com.bistro.module.game.domain.BetPeriodStatisticsVo;
import com.bistro.module.game.domain.GameBaseInfo;
import com.bistro.module.game.service.IBetPeriodStatisticsService;
import com.bistro.module.game.service.IGameBaseInfoService;
import com.bistro.module.merchant.domain.MerchantInfo;
import com.bistro.module.merchant.service.IMerchantInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bet_period_statistics")
public class BetPeriodStatisticsController extends BaseController {

    @Autowired
    IGameBaseInfoService gameBaseInfoService;

    @Autowired
    IMerchantInfoService merchantInfoService;

    @Autowired
    IBetPeriodStatisticsService betPeriodStatisticsService;


    @PreAuthorize("@ss.hasPermi('game:periodstatistics:list')")
    @GetMapping("/list")
    public List<BetPeriodStatisticsVo> list(BetPeriodStatisticsQuery query) {
        List<BetPeriodStatisticsVo> list = betPeriodStatisticsService.search(query);
        return list;
    }

    @GetMapping("/gameList")
    public List<GameBaseInfo> gameList() {
        GameBaseInfo gameBaseInfo = new GameBaseInfo();
        List<GameBaseInfo> list = gameBaseInfoService.selectGameBaseInfoList(gameBaseInfo);
        return list;
    }

    @GetMapping("/merchantList")
    public List<MerchantInfo> merchantList() {
        MerchantInfo merchantInfo = new MerchantInfo();
        List<MerchantInfo> list = merchantInfoService.selectMerchantInfoList(merchantInfo);
        return list;
    }
}
